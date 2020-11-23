package pl.pollub.bsi.infrastructure.security

import io.micronaut.context.event.ApplicationEventPublisher
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.*
import io.micronaut.transaction.SynchronousTransactionManager
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.vavr.control.Either
import io.vavr.control.Option
import org.reactivestreams.Publisher
import pl.pollub.bsi.domain.password.api.Encrypter
import pl.pollub.bsi.domain.user.port.UserRepository
import pl.pollub.bsi.infrastructure.event.AuthenticationFailedEvent
import pl.pollub.bsi.infrastructure.event.AuthenticationSuccessfulEvent
import pl.pollub.bsi.infrastructure.login_attempt.api.LastLoginAttempts
import pl.pollub.bsi.infrastructure.login_attempt.api.LoginHandlerFacade
import java.sql.Connection
import java.time.LocalDateTime
import java.util.*
import javax.inject.Singleton

@Singleton
internal class UserPasswordAuthenticationProvider(
        private val userRepository: UserRepository,
        private val loginHandlerFacade: LoginHandlerFacade,
        private val applicationEventPublisher: ApplicationEventPublisher,
        private val transactionManager: SynchronousTransactionManager<Connection>
) : AuthenticationProvider {

    companion object {
        val INDEFINITE_DATE: LocalDateTime = LocalDateTime.of(9999, 12, 31, 23, 59, 59)
    }

    override fun authenticate(httpRequest: HttpRequest<*>?, authenticationRequest: AuthenticationRequest<*, *>?): Publisher<AuthenticationResponse> {
        val username = authenticationRequest?.identity.toString()
        val password = authenticationRequest?.secret.toString()
        val ipAddress = httpRequest?.remoteAddress?.address?.toString()

        return Flowable.create({ emitter ->
            val resolvedBlockade = resolveUserBlockade(username, ipAddress!!)
            if (resolvedBlockade.isLeft) {
                emitter.onError(AuthenticationException(AuthenticationFailed(resolvedBlockade.left)))
                return@create
            }
            if (checkCredentials(username, password)) {
                publishLoginSuccessfulEvent(username, ipAddress)
                emitter.onNext(BsiAuthenticationResponse(
                        getLastLoginAttempts(getUserId(username)),
                        UserDetails(username, Collections.emptyList()))
                )
                emitter.onComplete()
            } else {
                publishLoginFailedEvent(username, ipAddress)
                emitter.onError(AuthenticationException(AuthenticationFailed("Wrong login or password.")))
            }
        }, BackpressureStrategy.ERROR)
    }

    private fun getLastLoginAttempts(userId: Long): LastLoginAttempts {
        return LastLoginAttempts(
                transactionManager.executeRead { loginHandlerFacade.getLastFailedLoginDate(userId) },
                LocalDateTime.now()
        )
    }

    private fun resolveUserBlockade(username: String, ipAddress: String): Either<String, *> {
        val lastLoginFailure = transactionManager.executeRead {
            loginHandlerFacade.getLastLoginFailure(findByUsername(username).map { it.id }.getOrElse { -1L }, ipAddress)
        }
        return Option.of(lastLoginFailure.accountBlockedTo)
                .filter { LocalDateTime.now().isBefore(it) }
                .map { Either.left<String, Any>("Account is blocked until: $it") }
                .getOrElse {
                    Option.of(lastLoginFailure.ipBlockedTo)
                            .filter { LocalDateTime.now().isBefore(it) }
                            .map { Either.left<String, Any>(resolveIpBlockadeMessage(it)) }
                            .getOrElse(Either.right(Any()))
                }
    }

    private fun resolveIpBlockadeMessage(ipBlockedUntil: LocalDateTime?): String {
        return if (INDEFINITE_DATE == ipBlockedUntil) {
            "IP is blocked permanently. Write a ticket do administrators to remove the blockade."
        } else {
            "IP is blocked until: $ipBlockedUntil"
        }
    }

    private fun publishLoginSuccessfulEvent(username: String, ipAddress: String) {
        applicationEventPublisher.publishEvent(AuthenticationSuccessfulEvent(this, getUserId(username), ipAddress))
    }

    private fun checkCredentials(username: String, password: String): Boolean {
        return transactionManager.executeRead {
            findByUsername(username)
                    .filter {
                        username.equals(it.login, true) &&
                                Encrypter.encrypt(
                                        it.algorithm.instance,
                                        password,
                                        it.salt
                                ).equals(it.password, true)
                    }
                    .isDefined
        }
    }

    private fun findByUsername(username: String) = transactionManager.executeRead { userRepository.findByLogin(username) }

    private fun publishLoginFailedEvent(username: String, remoteAddress: String?) {
        applicationEventPublisher.publishEvent(AuthenticationFailedEvent(this, getUserId(username), remoteAddress))
    }

    private fun getUserId(username: String): Long = findByUsername(username)
            .map { it.id }
            .orNull
}
package pl.pollub.bsi.infrastructure.security

import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.vavr.control.Option
import org.reactivestreams.Publisher
import pl.pollub.bsi.domain.api.Algorithm
import pl.pollub.bsi.domain.user.port.UserRepository
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.inject.Singleton

@Singleton
class UserPasswordAuthenticationProvider(
        private val userRepository: UserRepository
) : AuthenticationProvider {

    override fun authenticate(httpRequest: HttpRequest<*>?, authenticationRequest: AuthenticationRequest<*, *>?): Publisher<AuthenticationResponse> {
        val username = authenticationRequest?.identity.toString();
        val password = authenticationRequest?.secret.toString()

        return Flowable.create({ emitter ->
            if (checkCredentials(username, password)) {
                emitter.onNext(UserDetails(username, Collections.emptyList()))
                emitter.onComplete()
            }
            else emitter.onError(AuthenticationException(AuthenticationFailed("Wrong login or password.")))
        }, BackpressureStrategy.ERROR)
    }

    private fun checkCredentials(username: String, password: String): Boolean {
        return userRepository.findByLogin(username)
                .filter { username.equals(it.login, true) && hash(it.algorithm, password).equals(it.password, true) }
                .isDefined
    }

    private fun hash(algorithm: Algorithm, password: String): String {
        val messageDigest = MessageDigest.getInstance(algorithm.instance)
        messageDigest.update(password.toByteArray(StandardCharsets.UTF_8))
        return String.format("%064x", BigInteger(1, messageDigest.digest()))
    }
}
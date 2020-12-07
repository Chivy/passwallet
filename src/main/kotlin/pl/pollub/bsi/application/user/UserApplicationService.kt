package pl.pollub.bsi.application.user

import io.micronaut.context.annotation.Context
import io.micronaut.transaction.SynchronousTransactionManager
import io.vavr.collection.List
import io.vavr.collection.Traversable
import io.vavr.control.Either
import io.vavr.control.Option
import io.vavr.kotlin.toVavrList
import pl.pollub.bsi.application.AccessMode
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.application.password.api.PasswordUpdateCommand
import pl.pollub.bsi.application.user.api.CreateUserApplicationRequest
import pl.pollub.bsi.application.user.api.CreateUserApplicationResponse
import pl.pollub.bsi.application.user.api.UpdatePasswordApplicationRequest
import pl.pollub.bsi.domain.password.api.Encrypter
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.shares.api.SharesFacade
import pl.pollub.bsi.domain.user.api.*
import java.sql.Connection

@Context
internal class UserApplicationService(
        private val userFacade: UserFacade,
        private val passwordFacade: PasswordFacade,
        private val sharesFacade: SharesFacade,
        private val transactionManager: SynchronousTransactionManager<Connection>
) {

    internal fun save(createUserApplicationRequest: CreateUserApplicationRequest): Either<ErrorResponse, CreateUserApplicationResponse> {
        return transactionManager.executeWrite { transactionStatus ->
            val result = userFacade.create(UserCreationCommand.of(createUserApplicationRequest))
                    .flatMap { createPasswords(it, createUserApplicationRequest) }
                    .map { CreateUserApplicationResponse.of(it) }

            if (result.isLeft)
                transactionStatus.setRollbackOnly()
            result
        }
    }

    internal fun updatePassword(userId: Long, username: String, updatePasswordApplicationRequest: UpdatePasswordApplicationRequest, mode: AccessMode): Either<ErrorResponse, UserResponse> {
        return transactionManager.executeWrite {
            val preupdatedUser = userFacade.details(userId, username)

            return@executeWrite Option.of(updatePasswordApplicationRequest)
                    .filter { mode == AccessMode.MODIFY }
                    .toEither { ErrorResponse.readMode() }
                    .flatMap { userFacade.updatePassword(userId, username, UserPasswordUpdateCommand.of(it)) }
                    .flatMap { userResponse ->
                        preupdatedUser
                                .map {
                                    passwordFacade.update(userId, PasswordUpdateCommand(
                                            it.password,
                                            userResponse.password
                                    ))
                                }
                                .map { userResponse.withPasswords(passwordFacade.findByUserId(userId)) }
                    }
        }
    }

    internal fun details(userId: Long, username: String, passwordsDisclosed: Boolean): Either<ErrorResponse, UserResponse> {
        return transactionManager.executeRead {
            val passwords = passwordFacade.findByUserId(userId)
            userFacade.details(userId, username)
                    .map { userResponse ->
                        Option.of(passwords)
                                .filter { passwordsDisclosed }
                                .map { decryptPasswords(passwords, userResponse) }
                                .map { userResponse.withPasswords(it) }
                                .getOrElse { userResponse.withPasswords(passwords) }
                    }
                    .flatMap { user -> addSharedPasswords(user, passwordsDisclosed) }
        }
    }

    private fun addSharedPasswords(user: UserResponse, passwordsDisclosed: Boolean) =
            Either.sequenceRight(getSharedPasswords(user.id))
                    .map { sharedPasswords ->
                        user.copy(passwords =
                        user.passwords.appendAll(
                                sharedPasswords
                                        .toStream()
                                        .map { sharedPassword -> if (passwordsDisclosed) decrypt(sharedPassword) else sharedPassword }
                                        .toVavrList()
                        )
                        )
                    }

    private fun decrypt(sharedPassword: PasswordResponse): PasswordResponse? {
        return findPasswordOwner(sharedPassword.userId)
                ?.password
                ?.let { Encrypter.AES.decrypt(sharedPassword.password, it) }
                ?.let { sharedPassword.copy(password = it) }
    }

    private fun findPasswordOwner(userId: Long): UserResponse? {
        return userFacade.findById(userId).orNull
    }

    private fun createPasswords(user: UserResponse, createUserApplicationRequest: CreateUserApplicationRequest): Either<ErrorResponse, UserResponse> {
        return Either.sequenceRight(delegatePasswordCreation(createUserApplicationRequest, user))
                .map { user.withPasswords(List.ofAll(it)) }
    }

    private fun delegatePasswordCreation(createUserApplicationRequest: CreateUserApplicationRequest, user: UserResponse): List<Either<ErrorResponse, PasswordResponse>> {
        return Option.of(createUserApplicationRequest.passwords)
                .getOrElse { List.empty() }
                .toStream()
                .map { passwordFacade.create(user.id, PasswordCreationCommand.of(it, user.password)) }
                .toVavrList()
    }

    private fun decryptPasswords(passwords: Traversable<PasswordResponse>, userResponse: UserResponse) = passwords
            .toStream()
            .map { it.withPassword(Encrypter.AES.decrypt(it.password, userResponse.password)) }
            .toVavrList()


    private fun getSharedPasswords(userId: Long) =
            sharesFacade.findByUserId(userId)
                    .toStream()
                    .map { passwordFacade.findById(it.passwordId) }
                    .toVavrList()
}

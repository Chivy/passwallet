package pl.pollub.bsi.application.user

import io.micronaut.transaction.SynchronousTransactionManager
import io.vavr.collection.List
import io.vavr.control.Either
import io.vavr.control.Option
import io.vavr.kotlin.toVavrList
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.application.password.api.PasswordUpdateCommand
import pl.pollub.bsi.application.user.api.CreateUserApplicationRequest
import pl.pollub.bsi.application.user.api.CreateUserApplicationResponse
import pl.pollub.bsi.application.user.api.UpdatePasswordApplicationRequest
import pl.pollub.bsi.domain.password.api.Encrypter
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.api.*
import java.sql.Connection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UserApplicationService(
        @Inject private val userFacade: UserFacade,
        @Inject private val passwordFacade: PasswordFacade,
        @Inject private val transactionManager: SynchronousTransactionManager<Connection>
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

    internal fun updatePassword(userId: Long, username: String, updatePasswordApplicationRequest: UpdatePasswordApplicationRequest): Either<ErrorResponse, UserResponse> {
        return transactionManager.executeWrite {
            val preupdatedUser = userFacade.details(userId, username);

            return@executeWrite userFacade.updatePassword(userId, username, UserPasswordUpdateCommand.of(updatePasswordApplicationRequest))
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
                                .map {
                                    passwords
                                            .toStream()
                                            .map {
                                                it.withPassword(
                                                        Encrypter.AES.decrypt(
                                                                it.password,
                                                                userResponse.password
                                                        )
                                                )
                                            }
                                            .toVavrList()
                                }
                                .map { userResponse.withPasswords(it) }
                                .getOrElse { userResponse.withPasswords(passwords) }
                    }
        }
    }

    private fun createPasswords(user: UserResponse, createUserApplicationRequest: CreateUserApplicationRequest): Either<ErrorResponse, UserResponse> {
        val passwordCreationResponses = delegatePasswordCreation(createUserApplicationRequest, user)
        return passwordCreationResponses
                .toStream()
                .find { it.isLeft }
                .map { Either.left<ErrorResponse, UserResponse>(it.left) }
                .getOrElse {
                    Either.right(
                            user.withPasswords(
                                    passwordCreationResponses
                                            .toStream()
                                            .map { it.get() }
                                            .toVavrList()
                            )
                    )
                }
    }

    private fun delegatePasswordCreation(createUserApplicationRequest: CreateUserApplicationRequest, user: UserResponse): List<Either<ErrorResponse, PasswordResponse>> {
        return Option.of(createUserApplicationRequest.passwords)
                .getOrElse { List.empty() }
                .toStream()
                .map { passwordFacade.create(user.id, PasswordCreationCommand.of(it, user.password)) }
                .toVavrList()
    }


}

package pl.pollub.bsi.application.password

import io.micronaut.context.annotation.Context
import io.micronaut.transaction.SynchronousTransactionManager
import io.vavr.collection.List
import io.vavr.control.Either
import io.vavr.control.Option
import io.vavr.kotlin.toVavrList
import pl.pollub.bsi.application.AccessMode
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.password.api.CreatePasswordApplicationRequest
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.application.password.api.UpdatePasswordApplicationRequest
import pl.pollub.bsi.application.password.api.WalletPasswordUpdateCommand
import pl.pollub.bsi.domain.password.api.Encrypter
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.shares.api.SharesFacade
import pl.pollub.bsi.domain.user.api.PasswordResponse
import pl.pollub.bsi.domain.user.api.UserFacade
import pl.pollub.bsi.domain.user.api.UserResponse
import java.sql.Connection

@Context
internal class PasswordApplicationService(
    private val userFacade: UserFacade,
    private val passwordFacade: PasswordFacade,
    private val sharesFacade: SharesFacade,
    private val transactionManager: SynchronousTransactionManager<Connection>
) {
    fun create(userId: Long, username: String, request: CreatePasswordApplicationRequest): Either<ErrorResponse, PasswordResponse> {
        return transactionManager.executeWrite {
            userFacade.details(userId, username)
                .flatMap {
                    passwordFacade.create(
                        userId, PasswordCreationCommand(
                            request.login,
                            request.password,
                            request.webAddress,
                            request.description,
                            it.password
                        )
                    )
                }
        }
    }

    fun update(passwordId: Long, name: String, updatePasswordApplicationRequest: UpdatePasswordApplicationRequest): Either<ErrorResponse, PasswordResponse> {
        return transactionManager.executeWrite {
            val passwordById = passwordFacade.findById(passwordId)
            val user = userFacade.findByUsername(name).orNull
            passwordById
                .flatMap {
                    validateUserPermission(
                        user,
                        it.userId
                    )
                }
                .flatMap { passwordById }
                .flatMap {
                    passwordFacade.update(
                        WalletPasswordUpdateCommand(
                            user.id,
                            passwordId,
                            Encrypter.AES.encrypt(
                                updatePasswordApplicationRequest.password,
                                user.password
                            )
                        )
                    )
                }
        }
    }

    fun details(userId: Long, name: String, passwordId: Long): Either<ErrorResponse, PasswordResponse> {
        return transactionManager.executeRead {
            userFacade.details(userId, name)
                .flatMap { passwordFacade.findById(passwordId) }
        }
    }

    fun detailsList(userId: Long, name: String, passwordsDisclosed: Boolean): Either<ErrorResponse, List<PasswordResponse>> {
        return transactionManager.executeRead {
            val user = userFacade.findByUsername(name).orNull
            val sharedPasswords = getPasswordsWithShareds(user, userId, name)
            return@executeRead Option.of(passwordsDisclosed)
                .filter { it == true }
                .map { validateUserPermission(user, userId) }
                .map { sharedPasswords.map { decrypt(it) } }
                .getOrElse(sharedPasswords)
        }
    }

    fun delete(userId: Long, passwordId: Long, username: String, accessMode: AccessMode): Either<ErrorResponse, Long> {
        return transactionManager.executeWrite {
            validateUserPermission(
                findPasswordOwner(passwordId).orNull,
                userFacade.findByUsername(username).map { it.id }.orNull
            )
                .filterOrElse({ accessMode == AccessMode.MODIFY }) { ErrorResponse.readMode() }
                .map { sharesFacade.deleteByPasswordId(passwordId) }
                .map { passwordFacade.deleteByPasswordId(userId, passwordId) }
        }
    }

    private fun findPasswordOwner(passwordId: Long): Option<UserResponse> {
        return passwordFacade.findById(passwordId)
            .toOption()
            .map { it.userId }
            .flatMap { userFacade.findById(it) }


    }

    private fun getPasswordsWithShareds(user: UserResponse, userId: Long, name: String) =
        Option.of(user)
            .filter { it.id == userId }
            .toEither { ErrorResponse("User with username: $name has no permission to see passwords of user $userId") }
            .map { passwordFacade.findByUserId(userId) }
            .map {
                it.appendAll(
                    sharesFacade.findByUserId(userId)
                        .toStream()
                        .map { shared -> passwordFacade.findById(shared.passwordId) }
                        .map { result -> result.orNull }
                        .filter { result -> result != null }
                        .toVavrList()
                )
            }

    private fun validateUserPermission(user: UserResponse?, userId: Long): Either<ErrorResponse, *> {
        return Option.of(user)
            .filter { userId == it?.id }
            .toEither { ErrorResponse("User: ${user?.login} has no permission to manage passwords of user with ID: $userId") }
    }

    private fun decrypt(it: List<PasswordResponse>) = it
        .toStream()
        .map { password ->
            password.withPassword(
                Encrypter.AES.decrypt(
                    password.password,
                    findSharedPasswordMasterPassword(password.id)
                )
            )
        }
        .toVavrList()

    private fun findSharedPasswordMasterPassword(passwordId: Long): String {
        return passwordFacade.findById(passwordId)
            .toOption()
            .map { it.userId }
            .flatMap { userFacade.findById(it) }
            .map { it.password }
            .getOrElse { "" }
    }
}


package pl.pollub.bsi.application.password.api

import io.vavr.collection.List
import io.vavr.control.Either
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.api.PasswordResponse

interface PasswordFacade {
    fun create(userId: Long, passwordCreationCommand: PasswordCreationCommand) : Either<ErrorResponse, PasswordResponse>
    fun findByUserId(userId: Long): List<PasswordResponse>
    fun update(walletPasswordUpdateCommand: WalletPasswordUpdateCommand): Either<ErrorResponse, PasswordResponse>
    fun update(userId: Long, passwordUpdateCommand: PasswordUpdateCommand): Either<ErrorResponse, List<PasswordResponse>>
    fun findById(passwordId: Long): Either<ErrorResponse, PasswordResponse>
    fun deleteByPasswordId(userId: Long, passwordId: Long): Long
}

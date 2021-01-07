package pl.pollub.bsi.domain.password

import io.micronaut.context.annotation.Context
import io.vavr.collection.List
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.password.api.PasswordUpdateCommand
import pl.pollub.bsi.application.password.api.WalletPasswordUpdateCommand
import pl.pollub.bsi.domain.password.api.Encrypter
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.password.port.PasswordRepository
import pl.pollub.bsi.domain.user.api.PasswordResponse
import javax.inject.Inject

@Context
internal class PasswordService(@Inject private val passwordRepository: PasswordRepository) {
    fun create(userId: Long, passwordCreationCommand: PasswordCreationCommand): Either<ErrorResponse, PasswordResponse> {
        return Option.of(passwordCreationCommand)
                .map { it.toDomain(userId) }
                .map { it.withPassword(Encrypter.AES.encrypt(it.password, it.masterPassword)) }
            .map { passwordRepository.save(userId, it) }
            .toEither { ErrorResponse.unexpected() }
            .map { it.toResponse() }
    }

    fun findByUserId(userId: Long): List<PasswordResponse> {
        return passwordRepository.findByUserId(userId)
            .map { it.toResponse() }
    }

    fun update(passwordUpdateCommand: WalletPasswordUpdateCommand): Either<ErrorResponse, PasswordResponse> {
        return Either.right(
            passwordRepository.update(
                passwordId = passwordUpdateCommand.passwordId,
                password = passwordUpdateCommand.password
            )
                .toResponse()
        )
    }

    fun update(userId: Long, passwordUpdateCommand: PasswordUpdateCommand): Either<ErrorResponse, List<PasswordResponse>> {
        return passwordRepository.findByUserId(userId)
            .toStream()
            .map { it.withPassword(Encrypter.AES.decrypt(it.password, passwordUpdateCommand.oldMasterPassword)) }
            .map { it.withPassword(Encrypter.AES.encrypt(it.password, passwordUpdateCommand.newMasterPassword)) }
            .map { passwordRepository.update(it.passwordId, it.password) }
            .map { it.toResponse() }
            .toList()
            .transform {
                    if (it.isEmpty) {
                        return@transform Either.left(
                                ErrorResponse.notFoundById("password", userId)
                        )
                    }
                    Either.right(it)
                }
    }

    fun findById(passwordId: Long): Option<PasswordResponse> {
        return passwordRepository.findById(passwordId)
                .map { it.toResponse() }
    }

    fun deleteByPasswordId(passwordId: Long): Long {
        return passwordRepository.deleteByPasswordId(passwordId)
    }

}

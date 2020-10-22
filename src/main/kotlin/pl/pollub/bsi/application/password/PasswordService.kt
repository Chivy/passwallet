package pl.pollub.bsi.application.password

import io.micronaut.context.annotation.Context
import io.vavr.collection.List
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.password.Password
import pl.pollub.bsi.domain.password.api.Encrypter
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.password.port.PasswordRepository
import pl.pollub.bsi.domain.user.api.PasswordResponse
import javax.inject.Inject

@Context
internal class PasswordService(@Inject private val passwordRepository: PasswordRepository) {
    fun create(userId: Long, passwordCreationCommand: PasswordCreationCommand): Either<ErrorResponse, Password> {
        return Option.of(passwordCreationCommand)
                .map { it.toDomain(userId) }
                .map { it.withPassword(Encrypter.AES.encrypt(it.password)) }
                .map { passwordRepository.save(userId, it) }
                .toEither { ErrorResponse.unexpected() }
    }

    fun findByUserId(userId: Long): List<PasswordResponse> {
        return List.empty()
    }

}

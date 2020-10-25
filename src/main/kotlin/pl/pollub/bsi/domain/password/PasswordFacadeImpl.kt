package pl.pollub.bsi.domain.password

import io.micronaut.context.annotation.Context
import io.vavr.collection.List
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.password.PasswordService
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.application.password.api.PasswordUpdateCommand
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.api.PasswordResponse

@Context
internal class PasswordFacadeImpl(
        private val passwordService: PasswordService
) : PasswordFacade {
    override fun create(userId: Long, passwordCreationCommand: PasswordCreationCommand): Either<ErrorResponse, PasswordResponse> {
        return Option.of(passwordCreationCommand)
                .toEither { ErrorResponse.unexpected() }
                .flatMap { passwordService.create(userId, it) }

    }

    override fun findById(passwordId: Long): Either<ErrorResponse, PasswordResponse> {
        return passwordService.findById(passwordId)
                .toEither { ErrorResponse.notFoundById("password", passwordId) }
    }

    override fun update(userId: Long, passwordUpdateCommand: PasswordUpdateCommand): Either<ErrorResponse, List<PasswordResponse>> {
        return Option.of(passwordUpdateCommand)
                .toEither(ErrorResponse.unexpected())
                .flatMap { passwordService.update(userId, it) }
    }

    override fun findByUserId(userId: Long): List<PasswordResponse> {
        return passwordService.findByUserId(userId)
    }
}
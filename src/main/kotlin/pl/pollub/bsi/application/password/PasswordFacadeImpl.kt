package pl.pollub.bsi.application.password

import io.micronaut.context.annotation.Context
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.api.PasswordResponse

@Context
internal class PasswordFacadeImpl(
        private val passwordService: PasswordService
) : PasswordFacade {
    override fun create(userId: Long, passwordCreationCommand: PasswordCreationCommand): Either<ErrorResponse, PasswordResponse> {
        return Option.of(passwordCreationCommand)
                .toEither { ErrorResponse.unexpected() }
                .flatMap { passwordService.create(it) }
                .map { it.toResponse() }

    }
}
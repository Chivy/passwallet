package pl.pollub.bsi.application.password.api

import io.vavr.control.Either
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.password.Password
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.api.PasswordResponse

interface PasswordFacade {
    fun create(userId: Long, passwordCreationCommand: PasswordCreationCommand) : Either<ErrorResponse, PasswordResponse>

}

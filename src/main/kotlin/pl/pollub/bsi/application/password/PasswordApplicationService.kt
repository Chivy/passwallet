package pl.pollub.bsi.application.password

import io.micronaut.context.annotation.Context
import io.vavr.control.Either
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.password.api.CreatePasswordApplicationRequest
import pl.pollub.bsi.application.password.api.CreatePasswordApplicationResponse
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.api.PasswordResponse
import pl.pollub.bsi.domain.user.api.UserFacade

@Context
internal class PasswordApplicationService(
        private val userFacade: UserFacade,
        private val passwordFacade: PasswordFacade) {
    fun create(userId: Long, username: String, request: CreatePasswordApplicationRequest): Either<ErrorResponse, PasswordResponse> {
        return userFacade.details(userId, username)
                .flatMap {
                    passwordFacade.create(userId, PasswordCreationCommand(
                            request.login,
                            request.password,
                            request.webAddress,
                            request.description,
                            it.password
                    ))
                }
    }

    fun details(userId: Long, name: String, passwordId: Long): Either<ErrorResponse, PasswordResponse> {
        return userFacade.details(userId, name)
                .flatMap { passwordFacade.findById(passwordId) }
    }

}

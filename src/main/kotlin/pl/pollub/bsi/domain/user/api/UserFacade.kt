package pl.pollub.bsi.domain.user.api

import io.vavr.control.Either
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.user.User

interface UserFacade {
    fun create(userCreationCommand: UserCreationCommand) : Either<ErrorResponse, UserResponse>
}

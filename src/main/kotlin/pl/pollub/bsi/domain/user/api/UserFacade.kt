package pl.pollub.bsi.domain.user.api

import io.vavr.control.Either
import pl.pollub.bsi.application.error.ErrorResponse

interface UserFacade {
    fun create(userCreationCommand: UserCreationCommand) : Either<ErrorResponse, UserResponse>
    fun details(userId: Long, username: String): Either<ErrorResponse, UserResponse>
}

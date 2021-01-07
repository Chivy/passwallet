package pl.pollub.bsi.domain.user.api

import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse

interface UserFacade {
    fun create(userCreationCommand: UserCreationCommand) : Either<ErrorResponse, UserResponse>
    fun details(userId: Long, username: String): Either<ErrorResponse, UserResponse>
    fun updatePassword(userId: Long, username: String, userPasswordUpdateCommand: UserPasswordUpdateCommand): Either<ErrorResponse, UserResponse>
    fun findByUsername(username: String): Option<UserResponse>
    fun findById(userId: Long): Option<UserResponse>
}

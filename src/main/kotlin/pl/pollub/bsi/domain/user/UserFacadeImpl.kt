package pl.pollub.bsi.domain.user

import io.micronaut.context.annotation.Context
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserFacade
import pl.pollub.bsi.domain.user.api.UserPasswordUpdateCommand
import pl.pollub.bsi.domain.user.api.UserResponse

@Context
internal class UserFacadeImpl(
        private val userService: UserService,
) : UserFacade {
    override fun create(userCreationCommand: UserCreationCommand): Either<ErrorResponse, UserResponse> {
        return Option.of(userCreationCommand)
                .toEither(ErrorResponse.unexpected())
                .flatMap { userService.create(it) }
                .map { it.toResponse() }
    }

    override fun details(userId: Long, username: String): Either<ErrorResponse, UserResponse> {
        return userService.details(userId, username)
    }

    override fun updatePassword(userId: Long, name: String, userPasswordUpdateCommand: UserPasswordUpdateCommand): Either<ErrorResponse, UserResponse> {
        return userService.updatePassword(userId, name, userPasswordUpdateCommand)
                .map { it.toResponse() }
    }

    override fun findByUsername(username: String): Option<UserResponse> {
        return userService.findByUsername(username)
    }
}

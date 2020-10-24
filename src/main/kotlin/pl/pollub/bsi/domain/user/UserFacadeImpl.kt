package pl.pollub.bsi.domain.user

import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserResponse
import pl.pollub.bsi.domain.user.api.UserFacade
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UserFacadeImpl(
        @Inject private val userService: UserService,
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
}

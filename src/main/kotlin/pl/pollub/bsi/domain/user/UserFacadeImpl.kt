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
        @Inject private val userPasswordService: UserPasswordService
) : UserFacade {
    override fun create(userCreationCommand: UserCreationCommand): Either<ErrorResponse, UserResponse> {
        return Option.of(userCreationCommand)
                .map { userPasswordService.createHashed(it) }
                .map { userService.create(it) }
                .getOrElse { Either.left(ErrorResponse.unexpected()) }
    }
}

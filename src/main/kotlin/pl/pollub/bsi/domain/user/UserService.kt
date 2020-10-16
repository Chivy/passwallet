package pl.pollub.bsi.domain.user

import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserResponse
import pl.pollub.bsi.domain.user.port.UserRepository
import java.util.function.Supplier

internal class UserService(
        private val userRepository: UserRepository,
) {
    fun create(userCreationCommand: UserCreationCommand): Either<ErrorResponse, UserResponse> {
        return Option.of(userCreationCommand)
                .map(UserCreationCommand::toDomain)
                .map(userRepository::save)
                .map(User::toResponse)
                .toEither { ErrorResponse("Cannot create user") }
    }

}

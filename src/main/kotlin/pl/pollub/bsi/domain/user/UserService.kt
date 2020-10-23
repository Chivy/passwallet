package pl.pollub.bsi.domain.user

import io.micronaut.context.annotation.Context
import io.vavr.collection.List
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserResponse
import pl.pollub.bsi.domain.user.port.UserRepository
import javax.inject.Inject

@Context
internal class UserService(
        @Inject private val userRepository: UserRepository,
) {
    fun create(userCreationCommand: UserCreationCommand): Either<ErrorResponse, User> {
        return Option.of(userCreationCommand)
                .map { it.toDomain() }
                .filter { !userRepository.existsByLogin(it.login) }
                .toEither { ErrorResponse("User with login: ${userCreationCommand.login} already exists.") }
                .map { userRepository.save(it) }
                .map { user ->
                    User(
                            user.id,
                            user.login,
                            user.password,
                            user.algorithm,
                            user.salt,
                            user.isPasswordHashed,
                            user.passwords
                    )
                }
    }

    fun details(userId: Long): Either<ErrorResponse, UserResponse> {
        return userRepository.findById(userId)
                .toEither { ErrorResponse("User with ID: $userId not found.") }
                .map {
                    User(
                            it.id,
                            it.login,
                            it.password,
                            it.algorithm,
                            it.salt,
                            it.isPasswordHashed,
                            List.empty()
                    )
                }
                .map { it.toResponse() }
    }

}

package pl.pollub.bsi.domain.user

import io.micronaut.context.annotation.Context
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserResponse
import pl.pollub.bsi.domain.user.port.PasswordRepository
import pl.pollub.bsi.domain.user.port.UserRepository
import javax.inject.Inject

@Context
internal class UserService(
        @Inject private val userRepository: UserRepository,
        @Inject private val passwordRepository: PasswordRepository
) {
    fun create(userCreationCommand: UserCreationCommand): Either<ErrorResponse, UserResponse> {
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
                                    .toStream()
                                    .map { passwordRepository.save(it, user.id) }
                                    .toList()
                    )
                }
                .map { it.toResponse() }
    }

}

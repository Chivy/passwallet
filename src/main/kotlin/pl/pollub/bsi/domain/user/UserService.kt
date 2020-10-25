package pl.pollub.bsi.domain.user

import io.micronaut.context.annotation.Context
import io.vavr.collection.List
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.password.api.Encrypter
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserPasswordUpdateCommand
import pl.pollub.bsi.domain.user.api.UserResponse
import pl.pollub.bsi.domain.user.port.UserRepository
import javax.inject.Inject

@Context
internal class UserService(
        @Inject private val userRepository: UserRepository,
) {
    internal fun create(userCreationCommand: UserCreationCommand): Either<ErrorResponse, User> {
        return Option.of(userCreationCommand)
                .map { it.toDomain() }
                .filter { !userRepository.existsByLogin(it.login) }
                .toEither { ErrorResponse("User with login: ${userCreationCommand.login} already exists.") }
                .map {
                    it.withPassword(
                            Encrypter.encrypt(
                                    it.algorithm.instance,
                                    it.password,
                                    it.salt
                            )
                    )
                }
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

    internal fun updatePassword(userId: Long, name: String, command: UserPasswordUpdateCommand): Either<ErrorResponse, User> {
        return userRepository.findById(userId)
                .toEither(ErrorResponse.notFoundById("user", userId))
                .flatMap { checkUserPermission(name, it) }
                .map { it.withAlgorithm(command.algorithm) }
                .map { it.withNewSalt() }
                .map { it.withPassword(Encrypter.encrypt(it.algorithm.instance, command.password, it.salt)) }
                .map { userRepository.update(it) }
    }

    internal fun details(userId: Long, username: String): Either<ErrorResponse, UserResponse> {
        return userRepository.findById(userId)
                .toEither { ErrorResponse.notFoundById("user", userId) }
                .flatMap { checkUserPermission(username, it) }
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

    private fun checkUserPermission(username: String, user: User): Either<ErrorResponse, User> {
        return userRepository.findByLogin(username)
                .map { it.id }
                .filter { it.equals(user.id) }
                .map { Either.right<ErrorResponse, User>(user) }
                .getOrElse {
                    Either.left(
                            ErrorResponse(
                                    "User: $username has no permission to see passwords assigned to user with ID: ${user.id}"
                            )
                    )
                }
    }

}

package pl.pollub.bsi.application.user

import io.vavr.collection.List
import io.vavr.collection.Stream
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.api.CreateUserApplicationResponse
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.application.user.api.CreateUserApplicationRequest
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.User
import pl.pollub.bsi.domain.user.api.PasswordResponse
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserFacade
import pl.pollub.bsi.domain.user.api.UserResponse
import java.util.function.Function
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserApplicationService(
        @Inject private val userFacade: UserFacade,
        @Inject private val passwordFacade: PasswordFacade
) {
    fun save(createUserApplicationRequest: CreateUserApplicationRequest): Either<ErrorResponse, CreateUserApplicationResponse> {
        return userFacade.create(UserCreationCommand.of(createUserApplicationRequest))
                .map { it.withPasswords(createPasswords(createUserApplicationRequest)) }
                .map { CreateUserApplicationResponse.of(it) }
    }

    private fun createPasswords(createUserApplicationRequest: CreateUserApplicationRequest): List<PasswordResponse> {
        return Option.of(createUserApplicationRequest.passwords)
                .getOrElse { List.empty() }
                .toStream()
                .map { passwordFacade.create(PasswordCreationCommand.of(it)) }
                .toList()
    }

}

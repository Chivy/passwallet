package pl.pollub.bsi.application.user

import io.vavr.control.Either
import pl.pollub.bsi.application.api.CreateUserApplicationResponse
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.application.user.api.CreateUserApplicationRequest
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserFacade
import pl.pollub.bsi.domain.user.api.UserResponse
import java.util.function.Function
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserApplicationService(
        @Inject private val userFacade: UserFacade,
) {
    fun save(createUserApplicationRequest: CreateUserApplicationRequest): Either<ErrorResponse, CreateUserApplicationResponse> {
        return userFacade.create(
                UserCreationCommand.of(createUserApplicationRequest)
        )
                .map { CreateUserApplicationResponse.of(it) }
    }

}

package pl.pollub.bsi.application.user

import pl.pollub.bsi.application.api.CreateUserApplicationResponse
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.application.user.api.CreateUserApplicationRequest
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserFacade
import javax.inject.Singleton

@Singleton
class UserApplicationService(
        private val userFacade: UserFacade,
) {
    fun save(createUserApplicationRequest: CreateUserApplicationRequest): CreateUserApplicationResponse {
        return CreateUserApplicationResponse.of(
                userFacade.create(
                        UserCreationCommand.of(createUserApplicationRequest)
                )
        );
    }

}

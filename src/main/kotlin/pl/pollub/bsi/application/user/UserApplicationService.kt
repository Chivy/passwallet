package pl.pollub.bsi.application.user

import pl.pollub.bsi.application.api.CreateUserApplicationResponse
import pl.pollub.bsi.application.user.api.CreateUserApplicationRequest
import javax.inject.Singleton

@Singleton
class UserApplicationService(
        private val userFacade: UserFacade
) {
    fun save(createUserApplicationRequest: CreateUserApplicationRequest): CreateUserApplicationResponse {
        return CreateUserApplicationResponse("", );
    }

}

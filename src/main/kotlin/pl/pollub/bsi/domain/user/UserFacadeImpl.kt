package pl.pollub.bsi.domain.user

import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserCreationResponse
import pl.pollub.bsi.domain.user.api.UserFacade

class UserFacadeImpl(
        private val userService: UserService
) : UserFacade {
    override fun create(userCreationCommand: UserCreationCommand): UserCreationResponse {
        return UserCreationResponse()
    }
}

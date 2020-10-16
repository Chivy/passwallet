package pl.pollub.bsi.domain.user

import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserResponse
import pl.pollub.bsi.domain.user.api.UserFacade
import javax.inject.Singleton

@Singleton
internal class UserFacadeImpl(
        private val userService: UserService,
        private val passwordHashService: PasswordHashService
) : UserFacade {
    override fun create(userCreationCommand: UserCreationCommand): UserResponse {
        return userService.create(userCreationCommand)
    }
}

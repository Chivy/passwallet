package pl.pollub.bsi.domain.user.api

interface UserFacade {
    fun create(userCreationCommand: UserCreationCommand) : UserResponse
}

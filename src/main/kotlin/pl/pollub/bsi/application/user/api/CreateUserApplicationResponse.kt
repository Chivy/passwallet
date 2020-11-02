package pl.pollub.bsi.application.user.api

import pl.pollub.bsi.domain.user.api.UserResponse

data class CreateUserApplicationResponse(
        val id: Long,
        val login: String
) {

    companion object {
        fun of(userResponse: UserResponse) : CreateUserApplicationResponse {
            return CreateUserApplicationResponse(
                    userResponse.id,
                    userResponse.login
            )
        }
    }
}

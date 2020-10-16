package pl.pollub.bsi.application.api

import pl.pollub.bsi.domain.user.api.UserResponse

data class CreateUserApplicationResponse(
        private val login: String
) {

    companion object {
        fun of(userResponse: UserResponse) : CreateUserApplicationResponse {
            return CreateUserApplicationResponse(

            )
        }
    }
}

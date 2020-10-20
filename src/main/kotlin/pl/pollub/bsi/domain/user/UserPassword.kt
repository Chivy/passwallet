package pl.pollub.bsi.domain.user

import pl.pollub.bsi.domain.user.api.PasswordResponse

data class UserPassword(
        val login: String,
        val password: String,
        val webAddress: String,
        val description: String
) {
    fun toResponse(): PasswordResponse {
        return PasswordResponse(
                this.login,
                this.password,
                this.webAddress,
                this.description
        )
    }

}

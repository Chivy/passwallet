package pl.pollub.bsi.domain.user

import pl.pollub.bsi.domain.user.api.PasswordResponse

data class UserPassword(
        val id: Long,
        val login: String,
        val password: String,
        val webAddress: String,
        val description: String
) {
    fun toResponse(userId: Long): PasswordResponse {
        return PasswordResponse(
                this.id,
                userId,
                this.login,
                this.password,
                this.webAddress,
                this.description
        )
    }

}

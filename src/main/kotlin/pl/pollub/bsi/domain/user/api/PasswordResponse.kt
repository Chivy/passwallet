package pl.pollub.bsi.domain.user.api

data class PasswordResponse(
        val id: Long,
        val userId: Long,
        val login: String,
        val password: String,
        val webAddress: String,
        val description: String
) {

    fun withPassword(password: String) : PasswordResponse {
        return PasswordResponse(
                this.id,
                this.userId,
                this.login,
                password,
                this.webAddress,
                this.description
        )
    }
}


package pl.pollub.bsi.domain.password

import pl.pollub.bsi.domain.user.api.PasswordResponse

internal data class Password(
        val passwordId: PasswordId,
        val login: String,
        val password: String,
        val masterPassword: String?,
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

    fun withPassword(encryptedPassword: String): Password {
        return Password(
                this.passwordId,
                this.login,
                encryptedPassword,
                this.masterPassword,
                this.webAddress,
                this.description
        )
    }

}

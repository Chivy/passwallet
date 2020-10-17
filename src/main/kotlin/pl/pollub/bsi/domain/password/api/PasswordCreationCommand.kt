package pl.pollub.bsi.domain.password.api

import pl.pollub.bsi.application.password.api.CreatePasswordApplicationRequest
import pl.pollub.bsi.domain.user.Password

data class PasswordCreationCommand(
        val login: String,
        val password: String,
        val webAddress: String,
        val description: String
) {
    fun toDomain(): Password {
        return Password(
                this.login,
                this.password,
                this.webAddress,
                this.description
        )
    }

    companion object {
        fun of(applicationRequest: CreatePasswordApplicationRequest): PasswordCreationCommand {
            return PasswordCreationCommand(
                    applicationRequest.login,
                    applicationRequest.password,
                    applicationRequest.webAddress,
                    applicationRequest.description
            )
        }
    }
}


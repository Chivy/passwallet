package pl.pollub.bsi.domain.password.api

import pl.pollub.bsi.application.password.api.CreatePasswordApplicationRequest

data class PasswordCreationCommand(
        val login: String,
        val password: String,
        val webAddress: String,
        val description: String
) {

    companion object {
        fun of(applicationRequest: CreatePasswordApplicationRequest) : PasswordCreationCommand {
            return
        }
    }
}


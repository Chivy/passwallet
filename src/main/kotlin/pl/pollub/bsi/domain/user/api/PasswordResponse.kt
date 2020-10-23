package pl.pollub.bsi.domain.user.api

import javax.management.monitor.StringMonitor

data class PasswordResponse(
        val login: String,
        val password: String,
        val webAddress: String,
        val description: String
) {

    fun withPassword(password: String) : PasswordResponse {
        return PasswordResponse(
                this.login,
                password,
                this.webAddress,
                this.description
        )
    }
}


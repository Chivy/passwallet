package pl.pollub.bsi.application.password.api

data class CreatePasswordApplicationRequest(
        val login: String,
        val password: String,
        val webAddress: String,
        val description: String
)

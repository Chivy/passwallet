package pl.pollub.bsi.application.password.api

import pl.pollub.bsi.domain.api.Algorithm

data class CreatePasswordApplicationRequest(
        val login: String,
        val password: String,
        val algorithm: Algorithm,
        val webAddress: String,
        val description: String
)

package pl.pollub.bsi.application.user.api

import io.vavr.collection.List
import pl.pollub.bsi.application.password.api.CreatePasswordApplicationRequest
import pl.pollub.bsi.domain.user.api.Algorithm

data class CreateUserApplicationRequest(
        val login: String,
        val password: String,
        val algorithm: Algorithm,
        val passwords: List<CreatePasswordApplicationRequest>
)

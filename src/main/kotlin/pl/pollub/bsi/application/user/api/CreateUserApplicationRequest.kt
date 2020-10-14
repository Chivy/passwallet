package pl.pollub.bsi.application.user.api

import io.vavr.collection.List
import pl.pollub.bsi.application.password.api.CreatePasswordApplicationRequest
import pl.pollub.bsi.domain.api.Algorithm

data class CreateUserApplicationRequest(
        private val login: String,
        private val password: String,
        private val algorithm: Algorithm,
        private val passwords : List<CreatePasswordApplicationRequest>
) {}

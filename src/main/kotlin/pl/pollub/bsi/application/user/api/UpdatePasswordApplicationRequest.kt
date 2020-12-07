package pl.pollub.bsi.application.user.api

import pl.pollub.bsi.domain.user.api.Algorithm

internal data class UpdatePasswordApplicationRequest(
        val newPassword: String,
        val algorithm: Algorithm
)

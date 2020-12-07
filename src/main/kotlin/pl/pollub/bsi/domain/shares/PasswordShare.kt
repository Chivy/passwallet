package pl.pollub.bsi.domain.shares

import pl.pollub.bsi.application.shares.api.SharedPasswordResponse

internal data class PasswordShare(
        val id: Long?,
        val userId: Long,
        val passwordId: Long
) {
    fun toResponse(): SharedPasswordResponse {
        return SharedPasswordResponse(
                userId,
                passwordId
        )
    }
}

package pl.pollub.bsi.application.shares.api

data class CreatePasswordShareApplicationRequest(
        val userId: Long,
        val passwordId: Long
)

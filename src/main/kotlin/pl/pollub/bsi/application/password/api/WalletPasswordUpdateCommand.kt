package pl.pollub.bsi.application.password.api

data class WalletPasswordUpdateCommand(
    val userId: Long,
    val passwordId: Long,
    val password: String
)

package pl.pollub.bsi.application.password.api

data class PasswordUpdateCommand(
        val oldMasterPassword: String,
        val newMasterPassword: String,
)

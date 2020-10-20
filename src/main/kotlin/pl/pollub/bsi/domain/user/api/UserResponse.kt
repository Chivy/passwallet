package pl.pollub.bsi.domain.user.api

import io.vavr.collection.List
import pl.pollub.bsi.domain.api.Algorithm
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.UserPassword

data class UserResponse(
        val login: String,
        val password: String,
        val algorithm: Algorithm,
        val passwords: List<PasswordResponse>
) {
    fun withPasswords(passwords: List<PasswordResponse>): UserResponse {
        return UserResponse(
                this.login,
                this.password,
                this.algorithm,
                passwords
        )
    }
}
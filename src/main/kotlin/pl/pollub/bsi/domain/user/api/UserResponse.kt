package pl.pollub.bsi.domain.user.api

import io.vavr.collection.List
import pl.pollub.bsi.domain.api.Algorithm

data class UserResponse(
        val id: Long,
        val login: String,
        val password: String,
        val algorithm: Algorithm,
        val passwords: List<PasswordResponse>
) {
    fun withPasswords(passwords: List<PasswordResponse>): UserResponse {
        return UserResponse(
                this.id,
                this.login,
                this.password,
                this.algorithm,
                passwords
        )
    }
}
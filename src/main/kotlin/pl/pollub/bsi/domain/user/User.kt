package pl.pollub.bsi.domain.user

import io.vavr.collection.List
import pl.pollub.bsi.domain.api.Algorithm
import pl.pollub.bsi.domain.password.Password
import pl.pollub.bsi.domain.user.api.UserResponse

class User(
        val id: Long,
        val login: String,
        val password: String,
        val algorithm: Algorithm,
        val salt: String,
        val isPasswordHashed: Boolean,
        val passwords: List<UserPassword>
) {
    fun toResponse(): UserResponse {
        return UserResponse(
                this.login,
                this.password,
                this.algorithm,
                this.passwords
                        .toStream()
                        .map { it.toResponse() }
                        .toList()
        )
    }

    fun withPasswords(passwords: List<UserPassword>): User {
        return User(
                this.id,
                this.login,
                this.password,
                this.algorithm,
                this.salt,
                this.isPasswordHashed,
                passwords
        )
    }
}

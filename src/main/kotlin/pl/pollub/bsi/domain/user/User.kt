package pl.pollub.bsi.domain.user

import io.vavr.collection.List
import pl.pollub.bsi.domain.user.api.Algorithm
import pl.pollub.bsi.domain.user.api.UserResponse
import java.util.*

internal class User(
        val id: Long,
        val login: String,
        val password: String,
        val algorithm: Algorithm,
        val salt: String,
        val isPasswordHashed: Boolean,
        val passwords: List<UserPassword>
) {
    internal fun toResponse(): UserResponse {
        return UserResponse(
                this.id,
                this.login,
                this.password,
                this.algorithm,
                this.passwords
                        .toStream()
                        .map { it.toResponse(this.id) }
                        .toList()
        )
    }

    internal fun withPassword(password: String) : User {
        return User(
                this.id,
                this.login,
                password,
                this.algorithm,
                this.salt,
                this.isPasswordHashed,
                this.passwords
        )
    }

    fun withNewSalt(): User {
        return User(
                this.id,
                this.login,
                this.password,
                this.algorithm,
                UUID.randomUUID().toString(),
                this.algorithm == Algorithm.SHA_512,
                this.passwords
        )
    }

    fun withAlgorithm(algorithm: String): User {
        return User(
                this.id,
                this.login,
                this.password,
                Algorithm.valueOf(algorithm),
                this.salt,
                algorithm == Algorithm.SHA_512.instance,
                this.passwords

        )
    }
}

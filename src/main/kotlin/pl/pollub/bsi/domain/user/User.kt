package pl.pollub.bsi.domain.user

import io.vavr.collection.List
import pl.pollub.bsi.domain.api.Algorithm
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.api.UserResponse

class User(
        private val login: String,
        private val password: String,
        private val algorithm: Algorithm,
        private val passwords: List<PasswordCreationCommand>
) {
    fun toResponse(): UserResponse {
        return UserResponse(
                this.login,
                this.password,
                this.algorithm,
                this.passwords
        )
    }

}

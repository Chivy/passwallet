package pl.pollub.bsi.domain.user.api

import io.vavr.collection.List
import pl.pollub.bsi.application.user.api.CreateUserApplicationRequest
import pl.pollub.bsi.domain.api.Algorithm
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.User

data class UserCreationCommand(
        val login: String,
        val password: String,
        val algorithm: Algorithm,
        val passwords: List<PasswordCreationCommand>
) {
    fun toDomain() : User {
        return User(
                this.login,
                this.password,
                this.algorithm
        )
    }

    companion object {
        fun of(applicationRequest: CreateUserApplicationRequest): UserCreationCommand {
            return UserCreationCommand(
                    applicationRequest.login,
                    applicationRequest.password,
                    applicationRequest.algorithm,
                    applicationRequest
                            .passwords
                            .map(PasswordCreationCommand::of)
                            .toList()
            )
        }
    }
}

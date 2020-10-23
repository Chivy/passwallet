package pl.pollub.bsi.domain.user.api

import io.vavr.collection.List
import io.vavr.control.Option
import io.vavr.kotlin.toVavrList
import pl.pollub.bsi.application.user.api.CreateUserApplicationRequest
import pl.pollub.bsi.domain.api.Algorithm
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.User
import java.util.*

data class UserCreationCommand(
        val login: String,
        val password: String,
        val algorithm: Algorithm,
        val salt: String,
        val passwords: List<PasswordCreationCommand>
) {
    fun toDomain(): User {
        return User(
                0,
                this.login,
                this.password,
                this.algorithm,
                UUID.randomUUID().toString(),
                this.algorithm == Algorithm.SHA_512,
                this.passwords
                        .toStream()
                        .map { it.toUserDomain() }.toVavrList()
        )
    }

    companion object {
        fun of(applicationRequest: CreateUserApplicationRequest): UserCreationCommand {
            return UserCreationCommand(
                    applicationRequest.login,
                    applicationRequest.password,
                    applicationRequest.algorithm,
                    UUID.randomUUID().toString(),
                    applicationRequest
                            .passwords
                            .map(PasswordCreationCommand.Companion::of)
                            .toList()
            )
        }
    }
}

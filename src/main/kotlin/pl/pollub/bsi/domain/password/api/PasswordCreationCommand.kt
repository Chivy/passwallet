package pl.pollub.bsi.domain.password.api

import pl.pollub.bsi.application.password.api.CreatePasswordApplicationRequest
import pl.pollub.bsi.domain.password.Password
import pl.pollub.bsi.domain.password.PasswordId
import pl.pollub.bsi.domain.user.UserPassword

data class PasswordCreationCommand(
        val login: String,
        val password: String,
        val webAddress: String,
        val description: String,
        val masterPassword: String
) {
    fun toUserDomain(): UserPassword {
        return UserPassword(
                this.login,
                this.password,
                this.webAddress,
                this.description
        )
    }

    internal fun toDomain(userId: Long): Password {
        return Password(
                PasswordId(
                        null,
                        userId
                ),
                this.login,
                this.password,
                this.masterPassword,
                this.webAddress,
                this.description,
        )
    }

    companion object {
        fun of(applicationRequest: CreatePasswordApplicationRequest): PasswordCreationCommand {
            return of(applicationRequest, "")
        }

        fun of(applicationRequest: CreatePasswordApplicationRequest, masterPassword: String) : PasswordCreationCommand {
            return PasswordCreationCommand(
                    applicationRequest.login,
                    applicationRequest.password,
                    applicationRequest.webAddress,
                    applicationRequest.description,
                    masterPassword
            )

        }
    }
}


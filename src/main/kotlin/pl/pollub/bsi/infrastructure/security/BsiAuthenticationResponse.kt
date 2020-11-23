package pl.pollub.bsi.infrastructure.security

import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.security.authentication.UserDetails
import pl.pollub.bsi.infrastructure.login_attempt.api.LastLoginAttempts
import java.util.*

class BsiAuthenticationResponse(val lastLoginAttempts: LastLoginAttempts,
                                val userDetails: UserDetails) : AuthenticationResponse {
    override fun getUserDetails(): Optional<UserDetails> {
        return Optional.of(userDetails)
    }

    override fun getMessage(): Optional<String> {
        return Optional.of("Authenticated")
    }

}

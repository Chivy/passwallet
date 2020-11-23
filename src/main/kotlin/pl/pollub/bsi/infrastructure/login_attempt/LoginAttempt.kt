package pl.pollub.bsi.infrastructure.login_attempt

import java.time.LocalDateTime

class LoginAttempt(
        val id: Long?,
        val dateCreated: LocalDateTime,
        val userId: Long,
        val isSuccessful: Boolean,
        val blockedUntil: LocalDateTime?
)
package pl.pollub.bsi.infrastructure.login_attempt.api

import java.time.LocalDateTime

class LastLoginAttempts(val lastSuccessfulLogin: LocalDateTime?,
                        val lastFailedLogin: LocalDateTime)

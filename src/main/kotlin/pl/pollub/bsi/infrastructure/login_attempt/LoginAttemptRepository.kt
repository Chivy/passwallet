package pl.pollub.bsi.infrastructure.login_attempt

import io.vavr.collection.List
import io.vavr.control.Option

interface LoginAttemptRepository {

    fun save(loginAttempt: LoginAttempt)
    fun findByUserId(userId: Long): List<LoginAttempt>
    fun findLastFailureByUserId(userId: Long): Option<LoginAttempt>
    fun deleteByUserId(userId: Long)
}

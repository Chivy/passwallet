package pl.pollub.bsi.infrastructure.login_attempt.api

import java.time.LocalDateTime

interface LoginHandlerFacade {
    fun handleFailure(userId: Long, ipAddress: String?)
    fun getLastLoginFailure(userId: Long, ipAddress: String): BlockadeDto
    fun getLastFailedLoginDate(userId: Long): LocalDateTime?
    fun handleSuccess(userId: Long, ipAddress: String)
    fun removeIpBlockade(ipAddress: String?): Unit?
}

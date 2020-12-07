package pl.pollub.bsi.infrastructure.login_attempt

import io.micronaut.context.annotation.Context
import pl.pollub.bsi.infrastructure.login_attempt.api.BlockadeDto
import java.time.LocalDateTime

@Context
class LoginHandlerService(private val loginAttemptRepository: LoginAttemptRepository,
                          private val ipAuditRepository: IpAuditRepository) {
    fun handleFailure(userId: Long, ipAddress: String?) {
        loginAttemptRepository.save(buildLoginAttempt(userId))
        ipAuditRepository.save(buildIpAudit(ipAddress!!))
    }

    fun handleSuccess(userId: Long, ipAddress: String) {
        loginAttemptRepository.deleteByUserId(userId)
        ipAuditRepository.deleteByIpAddress(ipAddress)
        loginAttemptRepository.save(LoginAttempt(null, LocalDateTime.now(), userId, true, null))
        ipAuditRepository.save(IpAudit(null, LocalDateTime.now(), ipAddress, null, true))
    }

    fun getLastLoginFailure(userId: Long, ipAddress: String): BlockadeDto {
        return BlockadeDto(
                loginAttemptRepository.findLastFailureByUserId(userId).map { it.blockedUntil }.orNull,
                ipAuditRepository.findLastFailureByIp(ipAddress).map { it.blockedUntil }.orNull
        )
    }

    fun getLastFailedLoginDate(userId: Long): LocalDateTime? {
        return loginAttemptRepository.findLastFailureByUserId(userId)
                .map { it.dateCreated }
                .orNull
    }

    fun removeIpBlockade(ipAddress: String?): Unit? {
        return ipAddress?.let { ipAuditRepository.deleteByIpAddress(it) }
    }

    private fun buildLoginAttempt(userId: Long): LoginAttempt {
        val userLoginAttempts = countFailedLogins(userId)
        val now = LocalDateTime.now()
        return LoginAttempt(
                null,
                now,
                userId,
                false,
                calculateBlockadeTime(userLoginAttempts)
        )
    }

    private fun countFailedLogins(userId: Long) = loginAttemptRepository.findByUserId(userId)
            .toStream()
            .filter { !it.isSuccessful }
            .count() + 1

    private fun calculateBlockadeTime(loginAttempts: Int, byIP: Boolean = false): LocalDateTime? {
        val now = LocalDateTime.now()
        return when (loginAttempts) {
            0 -> null
            1 -> null
            2 -> now.plusSeconds(5)
            3 -> now.plusSeconds(10)
            else ->
                if (byIP)
                    LocalDateTime.of(9999, 12, 31, 23, 59, 59)
                else
                    now.plusMinutes(2)
        }
    }

    private fun buildIpAudit(ipAddress: String): IpAudit {
        val ipAuditsCount = countFailedIpLogins(ipAddress)
        val now = LocalDateTime.now()
        return IpAudit(
                null,
                now,
                ipAddress,
                calculateBlockadeTime(ipAuditsCount, true),
                false
        )
    }

    private fun countFailedIpLogins(ipAddress: String) = ipAuditRepository.findByIp(ipAddress)
            .toStream()
            .filter { !it.isSuccessful }
            .count() + 1
}

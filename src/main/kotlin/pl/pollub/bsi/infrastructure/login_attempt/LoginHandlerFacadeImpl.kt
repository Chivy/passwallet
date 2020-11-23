package pl.pollub.bsi.infrastructure.login_attempt

import io.micronaut.transaction.SynchronousTransactionManager
import pl.pollub.bsi.infrastructure.login_attempt.api.BlockadeDto
import pl.pollub.bsi.infrastructure.login_attempt.api.LoginHandlerFacade
import java.sql.Connection
import java.time.LocalDateTime
import javax.inject.Singleton

@Singleton
class LoginHandlerFacadeImpl(private val loginHandlerService: LoginHandlerService,
                             private val transactionManager: SynchronousTransactionManager<Connection>) : LoginHandlerFacade {
    override fun handleFailure(userId: Long, ipAddress: String?) {
        loginHandlerService.handleFailure(userId, ipAddress)
    }

    override fun handleSuccess(userId: Long, ipAddress: String) {
        return loginHandlerService.handleSuccess(userId, ipAddress)
    }

    override fun removeIpBlockade(ipAddress: String?): Unit? {
        return transactionManager.executeWrite { loginHandlerService.removeIpBlockade(ipAddress) }
    }

    override fun getLastLoginFailure(userId: Long, ipAddress: String): BlockadeDto {
        return loginHandlerService.getLastLoginFailure(userId, ipAddress)
    }

    override fun getLastFailedLoginDate(userId: Long): LocalDateTime? {
        return loginHandlerService.getLastFailedLoginDate(userId)
    }
}
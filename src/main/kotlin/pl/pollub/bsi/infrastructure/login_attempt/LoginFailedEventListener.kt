package pl.pollub.bsi.infrastructure.login_attempt

import io.micronaut.context.annotation.Context
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.transaction.SynchronousTransactionManager
import pl.pollub.bsi.infrastructure.event.AuthenticationFailedEvent
import pl.pollub.bsi.infrastructure.login_attempt.api.LoginHandlerFacade
import java.sql.Connection

@Context
class LoginFailedEventListener(
        private val loginHandlerService: LoginHandlerFacade,
        private val transactionManager: SynchronousTransactionManager<Connection>
) : ApplicationEventListener<AuthenticationFailedEvent> {

    override fun onApplicationEvent(event: AuthenticationFailedEvent) {
        transactionManager.executeWrite { loginHandlerService.handleFailure(event.userId, event.ipAddress) }
    }
}
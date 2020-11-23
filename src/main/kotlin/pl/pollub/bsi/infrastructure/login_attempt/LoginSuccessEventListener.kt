package pl.pollub.bsi.infrastructure.login_attempt

import io.micronaut.context.annotation.Context
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.transaction.SynchronousTransactionManager
import pl.pollub.bsi.infrastructure.event.AuthenticationSuccessfulEvent
import pl.pollub.bsi.infrastructure.login_attempt.api.LoginHandlerFacade
import java.sql.Connection

@Context
class LoginSuccessEventListener(private val loginHandlerFacade: LoginHandlerFacade,
                                private val transactionManager: SynchronousTransactionManager<Connection>)
    : ApplicationEventListener<AuthenticationSuccessfulEvent> {
    override fun onApplicationEvent(event: AuthenticationSuccessfulEvent) {
        transactionManager.executeWrite { loginHandlerFacade.handleSuccess(event.userId, event.ipAddress) }
    }
}
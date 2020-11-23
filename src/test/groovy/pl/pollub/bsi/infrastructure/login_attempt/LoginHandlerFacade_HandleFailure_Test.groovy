package pl.pollub.bsi.infrastructure.login_attempt

import io.vavr.collection.List
import pl.pollub.bsi.MockTransactionManager
import pl.pollub.bsi.application.user.adapter.InMemoryUserRepository
import pl.pollub.bsi.domain.api.Algorithm
import pl.pollub.bsi.domain.user.UserFacadeImpl
import pl.pollub.bsi.domain.user.UserService
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import spock.lang.Specification

import java.time.LocalDateTime

class LoginHandlerFacade_HandleFailure_Test extends Specification {
    def sut = new LoginHandlerFacadeImpl(
            new LoginHandlerService(
                    new InMemoryLoginAttemptRepository(),
                    new InMemoryIpAuditRepository()
            ),
            new MockTransactionManager()
    )

    def userFacade = new UserFacadeImpl(
            new UserService(new InMemoryUserRepository())
    )

    def LOGIN = "login123"
    def PASSWORD = "password"
    def IP_ADDRESS = "127.0.0.1"

    def "should block for not less than 5 secs when login failed two times"() {
        setup: "create user"
        def createdUser = userFacade.create(new UserCreationCommand(LOGIN, PASSWORD, Algorithm.SHA_512, UUID.randomUUID().toString(), List.empty())).get()

        and: "attempt to login - 2x failure"
        sut.handleFailure(createdUser.id, IP_ADDRESS)
        sut.handleFailure(createdUser.id, IP_ADDRESS)

        when: "querying for user blockade"
        def result = sut.getLastLoginFailure(createdUser.id, IP_ADDRESS)

        then: "account should be blocked for more than 5 secs"
        !LocalDateTime.now().minusSeconds(1).isAfter(result.accountBlockedTo)
        !LocalDateTime.now().minusSeconds(1).isAfter(result.ipBlockedTo)
    }

    def "should block for not less than 10 secs when login failed three times"() {
        setup: "create user"
        def createdUser = userFacade.create(new UserCreationCommand(LOGIN, PASSWORD, Algorithm.SHA_512, UUID.randomUUID().toString(), List.empty())).get()

        and: "attempt to login - 3x failure"
        sut.handleFailure(createdUser.id, IP_ADDRESS)
        sut.handleFailure(createdUser.id, IP_ADDRESS)
        sut.handleFailure(createdUser.id, IP_ADDRESS)

        when: "querying for user blockade"
        def result = sut.getLastLoginFailure(createdUser.id, IP_ADDRESS)

        then: "account should be blocked for more than 5 secs"
        !LocalDateTime.now().plusSeconds(5).isAfter(result.accountBlockedTo)
        !LocalDateTime.now().plusSeconds(5).isAfter(result.ipBlockedTo)
    }

    def "should block for not less than 2 mins when login failed five times"() {
        setup: "create user"
        def createdUser = userFacade.create(new UserCreationCommand(LOGIN, PASSWORD, Algorithm.SHA_512, UUID.randomUUID().toString(), List.empty())).get()

        and: "attempt to login - 5x failure"
        sut.handleFailure(createdUser.id, IP_ADDRESS)
        sut.handleFailure(createdUser.id, IP_ADDRESS)
        sut.handleFailure(createdUser.id, IP_ADDRESS)
        sut.handleFailure(createdUser.id, IP_ADDRESS)
        sut.handleFailure(createdUser.id, IP_ADDRESS)

        when: "querying for user blockade"
        def result = sut.getLastLoginFailure(createdUser.id, IP_ADDRESS)

        then: "account should be blocked for more than 2 mins and IP permanently"
        !LocalDateTime.now().plusMinutes(1).isAfter(result.accountBlockedTo)
        LocalDateTime.of(9999, 12, 31, 23, 59, 59) == result.ipBlockedTo
    }

}

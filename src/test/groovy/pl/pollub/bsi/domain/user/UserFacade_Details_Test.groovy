package pl.pollub.bsi.domain.user

import io.vavr.collection.List
import pl.pollub.bsi.application.user.adapter.InMemoryUserRepository
import pl.pollub.bsi.domain.password.api.Encrypter
import pl.pollub.bsi.domain.user.api.Algorithm
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserFacade
import spock.lang.Specification

class UserFacade_Details_Test extends Specification {
    UserFacade sut = new UserFacadeImpl(new UserService(new InMemoryUserRepository()))

    def "should return correct details when user has permission to view them"() {
        setup: "create user"
        final def command = new UserCreationCommand(
                "login",
                "password",
                Algorithm.HMAC,
                null,
                List.empty()
        )
        final savedUserId = sut.create(command).get().id

        when: "details method called with correct username"
        def details = sut.details(savedUserId, command.getLogin())

        then: "assert that correct details are returned"
        details.isRight()
        verifyAll(details.get()) {
            id == savedUserId
            login == command.login
            password == Encrypter.encrypt(command.algorithm.instance, command.password, null)

        }
    }

    def "should return error when another user is asking for details"() {
        setup: "create user"
        final def command = new UserCreationCommand(
                "login",
                "password",
                Algorithm.HMAC,
                null,
                List.empty()
        )
        final savedUserId = sut.create(command).get().id

        when: "details method called with incorrect username"
        def incorrectLogin = "abcbscbab"
        def details = sut.details(savedUserId, incorrectLogin)

        then: "assert that Either#left is returned and correct error message"
        details.isLeft()
        details.getLeft().message == "User: ${incorrectLogin} has no permission to see passwords assigned to user with ID: ${savedUserId}"
    }
}

package pl.pollub.bsi.domain.user

import io.micronaut.context.event.ApplicationEventPublisher
import io.vavr.collection.List
import pl.pollub.bsi.application.user.adapter.InMemoryUserRepository
import pl.pollub.bsi.domain.password.api.Encrypter
import pl.pollub.bsi.domain.user.api.Algorithm
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserFacade
import pl.pollub.bsi.domain.user.api.UserPasswordUpdateCommand
import spock.lang.Specification

class UserFacade_UpdatePassword_Test extends Specification {
    UserFacade sut = new UserFacadeImpl(
            new UserService(new InMemoryUserRepository()),
            Mock(ApplicationEventPublisher)
    )

    def "should update user's password, algorithm and salt"() {
        def salt = UUID.randomUUID().toString()

        setup: "create user"
        final command = new UserCreationCommand(
                "login",
                "password",
                Algorithm.SHA_512,
                salt,
                List.empty()
        )
        final savedUser = sut.create(command).get()

        and: "create user update command"
        final userUpdateCommand = new UserPasswordUpdateCommand("password123", "HMAC")

        when: "user update password method called"
        def updated = sut.updatePassword(savedUser.id, command.login, userUpdateCommand).get()

        then:
        verifyAll(updated) {
            id == savedUser.id
            login == savedUser.login
            password == Encrypter.encrypt("HMAC", userUpdateCommand.password, _ as String)
            password != Encrypter.encrypt("SHA-512", command.password, salt)
            algorithm.instance == userUpdateCommand.algorithm
        }
    }

    def "should return error when user to update doesn't exist"() {
        when: "update user's password called with non-existing ID"
        def userId = 124324132L
        def response = sut.updatePassword(userId, _ as String, GroovyStub(UserPasswordUpdateCommand.class))

        then: "assert returning error with error message"
        response.isLeft()
        response.getLeft().message == "User not found by ID: ${userId}"
    }

    def "should reject updating when user has no permission to update another user's password"() {
        def salt = UUID.randomUUID().toString()

        setup: "create user"
        final command = new UserCreationCommand(
                "login",
                "password",
                Algorithm.SHA_512,
                salt,
                List.empty()
        )
        final savedUser = sut.create(command).get()

        and: "create user update command"
        final userUpdateCommand = new UserPasswordUpdateCommand("password123", "HMAC")

        when: "user update password method called with wrong user"
        def wrongLogin = "fgdskjm"
        def updateResult = sut.updatePassword(savedUser.id, wrongLogin, GroovyStub(UserPasswordUpdateCommand.class))

        then: "assert returned error with error message"
        updateResult.isLeft()
        updateResult.getLeft().message == "User: ${wrongLogin} has no permission to see passwords assigned to user with ID: ${savedUser.id}"
    }
}

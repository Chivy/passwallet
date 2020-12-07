package pl.pollub.bsi.domain.password

import io.vavr.collection.List
import pl.pollub.bsi.application.password.adapter.InMemoryPasswordRepository
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.application.password.api.PasswordUpdateCommand
import pl.pollub.bsi.application.user.adapter.InMemoryUserRepository
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.UserFacadeImpl
import pl.pollub.bsi.domain.user.UserService
import pl.pollub.bsi.domain.user.api.Algorithm
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserFacade
import pl.pollub.bsi.domain.user.api.UserPasswordUpdateCommand
import spock.lang.Specification

class PasswordFacade_Update_Test extends Specification {
    PasswordFacade sut = new PasswordFacadeImpl(new PasswordService(new InMemoryPasswordRepository()))
    UserFacade userFacade = new UserFacadeImpl(new UserService(new InMemoryUserRepository()))

    def "should update password"() {
        def salt = UUID.randomUUID().toString()

        setup: "create user"
        final command = new UserCreationCommand(
                "login",
                "password",
                Algorithm.SHA_512,
                salt,
                List.empty()
        )
        final savedUser = userFacade.create(command).get()

        and: "create a password"
        def passwordCreationCommand = new PasswordCreationCommand(
                "login", "password", "wenaddress", "description", savedUser.password
        )
        def createdPassword = sut.create(savedUser.id, passwordCreationCommand).get()

        and: "update user's password"
        def userPasswordUpdateCommand = new UserPasswordUpdateCommand("drowssap", Algorithm.HMAC.instance)
        def updatedUser = userFacade.updatePassword(savedUser.id, command.login, userPasswordUpdateCommand).get()

        when: "password update called on saved user id"
        def passwordUpdateCommand = new PasswordUpdateCommand(savedUser.password, updatedUser.password)
        def updatedPassword = sut.update(savedUser.id, passwordUpdateCommand).get().first()

        then: "assert new hashed password does not match previously created password"
        createdPassword != updatedPassword

    }
}

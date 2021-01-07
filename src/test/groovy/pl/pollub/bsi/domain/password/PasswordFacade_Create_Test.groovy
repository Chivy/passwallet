package pl.pollub.bsi.domain.password

import io.micronaut.context.event.ApplicationEventPublisher
import pl.pollub.bsi.application.password.adapter.InMemoryPasswordRepository
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.domain.password.api.Encrypter
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import spock.lang.Specification

class PasswordFacade_Create_Test extends Specification {
    PasswordFacade sut = new PasswordFacadeImpl(
            new PasswordService(new InMemoryPasswordRepository()),
            Mock(ApplicationEventPublisher)
    )

    def "should create password"() {
        given: "password creation command"
        def passwordCreationCommand = new PasswordCreationCommand(
                "login", "password", "wenaddress", "description", "masterpassword"
        )

        when: "password create method called"
        def createdPassword = sut.create(1L, passwordCreationCommand).get()

        then: "properties should match the requested"
        verifyAll(createdPassword) {
            id != (Long) null
            userId == 1L
            login == passwordCreationCommand.login
            password == Encrypter.encrypt("AES", passwordCreationCommand.password, passwordCreationCommand.masterPassword)
            Encrypter.AES.decrypt(password, passwordCreationCommand.masterPassword) == passwordCreationCommand.password
            webAddress == passwordCreationCommand.webAddress
            description == passwordCreationCommand.description
        }
    }
}

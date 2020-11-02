package pl.pollub.bsi.domain.user


import io.vavr.collection.List
import pl.pollub.bsi.application.user.adapter.InMemoryUserRepository
import pl.pollub.bsi.domain.api.Algorithm
import pl.pollub.bsi.domain.password.api.Encrypter
import pl.pollub.bsi.domain.user.UserFacadeImpl
import pl.pollub.bsi.domain.user.UserService
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserFacade
import spock.lang.Shared
import spock.lang.Specification

class UserFacade_CreateUser_Test extends Specification {
    UserFacade sut = new UserFacadeImpl(new UserService(new InMemoryUserRepository()))

    @Shared
    def salt = UUID.randomUUID().toString()

    def "should create user when passed login that does not exist in the database"() {
        setup: "create command without passwords"
        def createUserCommand = new UserCreationCommand(
                "login", "password", Algorithm.SHA_512, salt, List.empty()
        )
        when: "Facade method called for saving a user"
        def savedUser = sut.create(createUserCommand).get()

        then: "saved user's properties should match the requested one"
        verifyAll(savedUser) {
            id != (Long) null
            login == createUserCommand.login
            password == Encrypter.encrypt(createUserCommand.algorithm.instance, createUserCommand.password, salt)
            algorithm == Algorithm.SHA_512
            passwords == List.empty()
        }
    }

    def "should return error when trying to create user with the same login"() {
        setup: "create user creation command"
        def userCreationCommand = new UserCreationCommand(
                "login", "password", Algorithm.SHA_512, salt, List.empty()
        )

        and: "UserFacade method called for saving first user"
        sut.create(userCreationCommand)

        when: "UserFacade save method called again with the same command"
        def result = sut.create(userCreationCommand)

        then: "assert that result is equal to Either#left and there is specified error message"
        result.isLeft()
        result.getLeft().message == "User with login: ${userCreationCommand.login} already exists."
    }
}

package pl.pollub.bsi.application.user


import io.vavr.collection.List
import io.vavr.control.Either
import pl.pollub.bsi.MockTransactionManager
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.application.user.adapter.InMemoryUserRepository
import pl.pollub.bsi.application.user.api.CreateUserApplicationRequest
import pl.pollub.bsi.application.user.api.CreateUserApplicationResponse
import pl.pollub.bsi.domain.api.Algorithm
import pl.pollub.bsi.domain.user.UserFacadeImpl
import pl.pollub.bsi.domain.user.UserService
import spock.lang.Specification

class UserApplicationService_Create_Test extends Specification {
    PasswordFacade passwordFacade = Mock()

    UserApplicationService sut = new UserApplicationService(
            new UserFacadeImpl(new UserService(new InMemoryUserRepository())),
            passwordFacade,
            new MockTransactionManager()
    )

    def "should create user without passwords"() {
        given: "user application request"
        CreateUserApplicationRequest createUserApplicationRequest = createUserApplicationRequest()

        when: "save method called"
        def savedUser = sut.save$passwallet(createUserApplicationRequest).get()

        and: "mock passwordFacade call"
        passwordFacade.create(_, _) >> Either.right(null)

        then: "should return login and id of created user"
        savedUser.id != (Long) null
        savedUser.login == createUserApplicationRequest.login
    }

    def "should return error with message when user with login already exists"() {
        def login = "useruser"

        setup:
        "create user with login: ${login}"
        sut.save$passwallet(createUserApplicationRequest(login))

        and: "mock passwordFacade"
        passwordFacade.create(_, _) >> Either.right(null)

        when:
        "save new user with same login: ${login}"
        Either<ErrorResponse, CreateUserApplicationResponse> result = sut.save$passwallet(createUserApplicationRequest(login))

        then:
        result.isLeft()
        result.getLeft().getMessage() == "User with login: ${login} already exists."
    }

    private static CreateUserApplicationRequest createUserApplicationRequest(final String login = "login") {
        new CreateUserApplicationRequest(
                login, "password", Algorithm.SHA_512, List.empty()
        )
    }
}

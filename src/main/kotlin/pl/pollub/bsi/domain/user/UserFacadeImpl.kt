package pl.pollub.bsi.domain.user

import io.micronaut.context.annotation.Context
import io.micronaut.context.event.ApplicationEventPublisher
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.archives.FunctionRunEvent
import pl.pollub.bsi.application.archives.UserPasswordChangedEvent
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.archives.api.FunctionName
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import pl.pollub.bsi.domain.user.api.UserFacade
import pl.pollub.bsi.domain.user.api.UserPasswordUpdateCommand
import pl.pollub.bsi.domain.user.api.UserResponse

@Context
internal class UserFacadeImpl(
    private val userService: UserService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : UserFacade {
    override fun create(userCreationCommand: UserCreationCommand): Either<ErrorResponse, UserResponse> {
        return Option.of(userCreationCommand)
            .toEither(ErrorResponse.unexpected())
            .flatMap { userService.create(it) }
            .peek { applicationEventPublisher.publishEvent(FunctionRunEvent(this, it.id, FunctionName.USER_REGISTERED)) }
            .map { it.toResponse() }
    }

    override fun details(userId: Long, username: String): Either<ErrorResponse, UserResponse> {
        return userService.details(userId, username)
            .peek { applicationEventPublisher.publishEvent(FunctionRunEvent(this, it.id, FunctionName.USER_DETAILS_RETURNED)) }
    }

    override fun updatePassword(userId: Long, username: String, userPasswordUpdateCommand: UserPasswordUpdateCommand): Either<ErrorResponse, UserResponse> {
        val preupdatedUser = userService.details(userId, username)
        return userService.updatePassword(userId, username, userPasswordUpdateCommand)
            .peek {
                preupdatedUser.peek { user ->
                    applicationEventPublisher.publishEvent(
                        UserPasswordChangedEvent(this, UserPasswordChangedEvent.Data(userId, user.id, user.password))
                    )
                }
            }
            .peek { applicationEventPublisher.publishEvent(FunctionRunEvent(this, it.id, FunctionName.USER_PASSWORD_CHANGED)) }
            .map { it.toResponse() }
    }

    override fun findByUsername(username: String): Option<UserResponse> = userService.findByUsername(username)
        .peek { sendGetUserDetailsEvent(it.id) }

    override fun findById(userId: Long): Option<UserResponse> = userService.findById(userId)
        .peek { sendGetUserDetailsEvent(it.id) }

    private fun sendGetUserDetailsEvent(userId: Long) =
        applicationEventPublisher.publishEvent(FunctionRunEvent(this, userId, FunctionName.USER_DETAILS_RETURNED))
}

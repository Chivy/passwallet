package pl.pollub.bsi.domain.password

import io.micronaut.context.annotation.Context
import io.micronaut.context.event.ApplicationEventPublisher
import io.vavr.collection.List
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.archives.FunctionRunEvent
import pl.pollub.bsi.application.archives.PasswordDeletedEvent
import pl.pollub.bsi.application.archives.PasswordUpdatedEvent
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.application.password.api.PasswordUpdateCommand
import pl.pollub.bsi.application.password.api.WalletPasswordUpdateCommand
import pl.pollub.bsi.domain.archives.api.FunctionName
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.api.PasswordResponse

@Context
internal class PasswordFacadeImpl(
    private val passwordService: PasswordService,
    private val applicationEventPublisher: ApplicationEventPublisher
) : PasswordFacade {
    override fun create(userId: Long, passwordCreationCommand: PasswordCreationCommand): Either<ErrorResponse, PasswordResponse> {
        return Option.of(passwordCreationCommand)
            .toEither { ErrorResponse.unexpected() }
            .flatMap { passwordService.create(userId, it) }
            .peek { applicationEventPublisher.publishEvent(FunctionRunEvent(this, userId, FunctionName.PASSWORD_CREATED)) }

    }

    override fun findById(passwordId: Long): Either<ErrorResponse, PasswordResponse> {
        return passwordService.findById(passwordId)
            .toEither { ErrorResponse.notFoundById("password", passwordId) }
    }

    override fun update(walletPasswordUpdateCommand: WalletPasswordUpdateCommand): Either<ErrorResponse, PasswordResponse> {
        val passwordId = walletPasswordUpdateCommand.passwordId
        val userId = walletPasswordUpdateCommand.userId
        return passwordService.update(walletPasswordUpdateCommand)
            .peek { applicationEventPublisher.publishEvent(FunctionRunEvent(this, userId, FunctionName.PASSWORD_UPDATED)) }
            .peek {
                applicationEventPublisher.publishEvent(
                    PasswordUpdatedEvent(
                        this,
                        PasswordUpdatedEvent.Data(
                            userId,
                            passwordId,
                            walletPasswordUpdateCommand.password
                        )
                    )
                )
            }
            .toEither { ErrorResponse.notFoundById("password", passwordId) }
    }

    override fun deleteByPasswordId(userId: Long, passwordId: Long): Long {
        val passwordToDelete = passwordService.findById(userId)
        return passwordService.deleteByPasswordId(passwordId)
            .apply { passwordToDelete.peek { applicationEventPublisher.publishEvent(PasswordDeletedEvent(this, PasswordDeletedEvent.Data(it))) } }
            .apply { applicationEventPublisher.publishEvent(FunctionRunEvent(this, userId, FunctionName.PASSWORD_DELETED)) }
    }

    override fun update(userId: Long, passwordUpdateCommand: PasswordUpdateCommand): Either<ErrorResponse, List<PasswordResponse>> {
        return Option.of(passwordUpdateCommand)
            .toEither(ErrorResponse.unexpected())
            .flatMap { passwordService.update(userId, it) }
    }

    override fun findByUserId(userId: Long): List<PasswordResponse> {
        return passwordService.findByUserId(userId)
    }
}
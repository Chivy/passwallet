package pl.pollub.bsi.application.shares

import io.micronaut.context.annotation.Context
import io.micronaut.transaction.SynchronousTransactionManager
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.password.api.PasswordFacade
import pl.pollub.bsi.application.shares.api.CreatePasswordShareApplicationRequest
import pl.pollub.bsi.application.shares.api.SharedPasswordResponse
import pl.pollub.bsi.domain.shares.api.SharePasswordCommand
import pl.pollub.bsi.domain.shares.api.SharesFacade
import pl.pollub.bsi.domain.user.api.UserFacade
import java.sql.Connection

@Context
internal class SharesApplicationService(private val userFacade: UserFacade,
                                        private val passwordFacade: PasswordFacade,
                                        private val sharesFacade: SharesFacade,
                                        private val txManager: SynchronousTransactionManager<Connection>) {

    fun share(name: String, createPasswordShare: CreatePasswordShareApplicationRequest): Either<ErrorResponse, SharedPasswordResponse> {
        return txManager.executeWrite {
            validateUserPermission(name, createPasswordShare)
                    .flatMap { sharesFacade.share(SharePasswordCommand(createPasswordShare.userId, createPasswordShare.passwordId)) }
        }
    }

    private fun validateUserPermission(name: String, createPasswordShare: CreatePasswordShareApplicationRequest) =
            Option.of(name)
                    .flatMap { userFacade.findByUsername(it) }
                    .map { it.id }
                    .flatMap { findMatchingPassword(it, createPasswordShare) }
                    .toEither { ErrorResponse("User with username: $name has no permission to share password with ID: ${createPasswordShare.passwordId}") }

    private fun findMatchingPassword(it: Long, createPasswordShare: CreatePasswordShareApplicationRequest) =
            passwordFacade.findByUserId(it)
                    .toStream()
                    .map { pass -> pass.id }
                    .find { passwordId -> passwordId == createPasswordShare.passwordId }
}

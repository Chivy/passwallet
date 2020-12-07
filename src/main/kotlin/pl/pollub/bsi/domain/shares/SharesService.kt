package pl.pollub.bsi.domain.shares

import io.micronaut.context.annotation.Context
import io.vavr.collection.List
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.shares.api.SharePasswordCommand
import pl.pollub.bsi.domain.shares.port.SharesRepository

@Context
internal class SharesService(private val sharesRepository: SharesRepository) {
    internal fun share(sharePasswordCommand: SharePasswordCommand): Either<ErrorResponse, PasswordShare> {
        return Option.of(sharePasswordCommand)
                .map { PasswordShare(null, sharePasswordCommand.userId, sharePasswordCommand.passwordId) }
                .map { sharesRepository.save(it) }
                .toEither { ErrorResponse.unexpected() }
    }

    fun findByUserId(userId: Long): List<PasswordShare> {
        return sharesRepository.findByUserId(userId)
    }

    fun deleteByPasswordId(passwordId: Long): List<Long> {
        return sharesRepository.deleteByPasswordId(passwordId)
    }
}

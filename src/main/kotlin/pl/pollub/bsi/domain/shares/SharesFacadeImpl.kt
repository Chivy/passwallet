package pl.pollub.bsi.domain.shares

import io.micronaut.context.annotation.Context
import io.vavr.collection.List
import io.vavr.control.Either
import io.vavr.kotlin.toVavrList
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.shares.api.SharedPasswordResponse
import pl.pollub.bsi.domain.shares.api.SharePasswordCommand
import pl.pollub.bsi.domain.shares.api.SharesFacade

@Context
internal class SharesFacadeImpl(private val sharesService: SharesService) : SharesFacade {
    override fun share(sharePasswordCommand: SharePasswordCommand): Either<ErrorResponse, SharedPasswordResponse> {
        return sharesService.share(sharePasswordCommand)
                .map { it.toResponse() }
    }

    override fun findByUserId(userId: Long): List<SharedPasswordResponse> {
        return sharesService.findByUserId(userId)
                .toStream()
                .map { it.toResponse() }
                .toVavrList()
    }

    override fun deleteByPasswordId(passwordId: Long): List<Long> {
        return sharesService.deleteByPasswordId(passwordId)
    }
}
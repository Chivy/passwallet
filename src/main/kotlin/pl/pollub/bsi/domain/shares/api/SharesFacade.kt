package pl.pollub.bsi.domain.shares.api

import io.vavr.collection.List
import io.vavr.control.Either
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.application.shares.api.SharedPasswordResponse

interface SharesFacade {
    fun share(sharePasswordCommand: SharePasswordCommand): Either<ErrorResponse, SharedPasswordResponse>
    fun findByUserId(userId: Long): List<SharedPasswordResponse>
    fun deleteByPasswordId(passwordId: Long): List<Long>

}

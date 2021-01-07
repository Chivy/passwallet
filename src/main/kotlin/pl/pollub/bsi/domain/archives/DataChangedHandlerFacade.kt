package pl.pollub.bsi.domain.archives

import io.micronaut.context.annotation.Context
import pl.pollub.bsi.application.archives.DataChangedEvent
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.archives.api.DataChangeResponse

@Context
class DataChangedHandlerFacade(private val dataChangedStrategyFactory: DataChangedHandlerStrategyFactory) {
    fun handle(dataChangedEvent: DataChangedEvent<DataChangedEvent.Data>): DataChangeResponse {
        return dataChangedStrategyFactory.getBy(dataChangedEvent.getType())
            .toEither { ErrorResponse.unexpected() }
            .flatMap { it.handle(dataChangedEvent.getEventContent()) }
            .getOrElseThrow { error -> RuntimeException(error.message) }
    }

}

package pl.pollub.bsi.domain.archives

import io.vavr.control.Either
import pl.pollub.bsi.application.archives.DataChangedEvent
import pl.pollub.bsi.application.archives.dto.DataChangedEventType
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.archives.api.DataChangeResponse

interface DataChangedHandler<in T : DataChangedEvent.Data> {

    fun handle(content: T): Either<ErrorResponse, DataChangeResponse>

    fun isApplicableFor(eventType: DataChangedEventType): Boolean

    interface DataChangedEventData
}

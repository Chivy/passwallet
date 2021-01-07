package pl.pollub.bsi.domain.archives

import io.micronaut.context.annotation.Context
import io.vavr.control.Option
import io.vavr.kotlin.toVavrStream
import pl.pollub.bsi.application.archives.DataChangedEvent
import pl.pollub.bsi.application.archives.dto.DataChangedEventType

@Context
class DataChangedHandlerStrategyFactory(private val dataChangedHandlers: List<DataChangedHandler<DataChangedEvent.Data>>) {

    fun getBy(eventContent: DataChangedEventType): Option<DataChangedHandler<DataChangedEvent.Data>> {
        return dataChangedHandlers.toVavrStream()
            .find { handler -> handler.isApplicableFor(eventContent) }
    }
}

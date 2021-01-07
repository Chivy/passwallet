package pl.pollub.bsi.application.archives

import io.micronaut.context.annotation.Context
import io.micronaut.runtime.event.annotation.EventListener
import pl.pollub.bsi.domain.archives.DataChangedHandlerFacade

@Context
class DataChangedEventListener(private val dataChangedHandlerFacade: DataChangedHandlerFacade) {

    @EventListener
    fun handlePasswordUpdated(dataChangedEvent: DataChangedEvent<DataChangedEvent.Data>) = dataChangedHandlerFacade.handle(dataChangedEvent)
}
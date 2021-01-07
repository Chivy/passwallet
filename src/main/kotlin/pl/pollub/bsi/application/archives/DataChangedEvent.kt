package pl.pollub.bsi.application.archives

import pl.pollub.bsi.application.archives.dto.DataChangedEventType

interface DataChangedEvent<out T : DataChangedEvent.Data> {
    fun getType(): DataChangedEventType

    fun getEventContent(): T

    interface Data
}

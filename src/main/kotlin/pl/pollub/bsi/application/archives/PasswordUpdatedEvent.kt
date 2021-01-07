package pl.pollub.bsi.application.archives

import io.micronaut.context.event.ApplicationEvent
import pl.pollub.bsi.application.archives.dto.DataChangedEventType

class PasswordUpdatedEvent(
    source: Any,
    val data: Data
) : ApplicationEvent(source), DataChangedEvent<PasswordUpdatedEvent.Data> {
    override fun getType(): DataChangedEventType = DataChangedEventType.PASSWORD_UPDATED

    override fun getEventContent(): Data = this.data

    data class Data(
        val userId: Long,
        val modifiedRecordId: Long,
        val oldPassword: String
    ) : DataChangedEvent.Data
}
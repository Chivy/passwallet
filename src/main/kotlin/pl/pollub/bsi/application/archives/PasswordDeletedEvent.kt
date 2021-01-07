package pl.pollub.bsi.application.archives

import io.micronaut.context.event.ApplicationEvent
import pl.pollub.bsi.application.archives.dto.DataChangedEventType
import pl.pollub.bsi.domain.user.api.PasswordResponse

class PasswordDeletedEvent(
    source: Any,
    private val data: Data
) : ApplicationEvent(source), DataChangedEvent<PasswordDeletedEvent.Data> {
    override fun getType(): DataChangedEventType = DataChangedEventType.PASSWORD_DELETED

    override fun getEventContent(): Data = this.data

    data class Data(val deletedPassword: PasswordResponse) : DataChangedEvent.Data
}

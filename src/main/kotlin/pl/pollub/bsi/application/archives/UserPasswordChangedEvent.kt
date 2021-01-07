package pl.pollub.bsi.application.archives

import io.micronaut.context.event.ApplicationEvent
import pl.pollub.bsi.application.archives.dto.DataChangedEventType

class UserPasswordChangedEvent(
    source: Any,
    private val userPasswordChangedData: Data
) : ApplicationEvent(source), DataChangedEvent<UserPasswordChangedEvent.Data> {
    override fun getType(): DataChangedEventType = DataChangedEventType.USER_PASSWORD_CHANGED

    override fun getEventContent(): Data = userPasswordChangedData

    data class Data(
        val userId: Long,
        val modifiedRecordId: Long,
        val oldPassword: String
    ) : DataChangedEvent.Data
}

package pl.pollub.bsi.domain.archives

import pl.pollub.bsi.application.archives.UserPasswordChangedEvent
import pl.pollub.bsi.application.archives.dto.DataChangedEventType
import pl.pollub.bsi.domain.archives.api.DataChangeResponse
import pl.pollub.bsi.domain.archives.api.TableName

data class DataChange(
    val id: Long?,
    val userId: Long,
    val modifiedRecordId: Long,
    val previousValueOfRecord: String,
    val actionType: DataChangedEventType,
    val tableName: TableName
) {
    fun toResponse() = DataChangeResponse(
        userId, previousValueOfRecord, actionType, tableName
    )

    companion object Factory {
        fun userPasswordChanged(data: UserPasswordChangedEvent.Data) = DataChange(
            null,
            data.userId,
            data.modifiedRecordId,
            data.oldPassword,
            DataChangedEventType.USER_PASSWORD_CHANGED,
            TableName.USER
        )
    }
}

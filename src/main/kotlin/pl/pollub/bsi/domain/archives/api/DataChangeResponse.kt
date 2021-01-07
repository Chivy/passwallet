package pl.pollub.bsi.domain.archives.api

import pl.pollub.bsi.application.archives.dto.DataChangedEventType

data class DataChangeResponse(
    val userId: Long,
    val previousValueOfRecord: String,
    val actionType: DataChangedEventType,
    val tableName: TableName
)

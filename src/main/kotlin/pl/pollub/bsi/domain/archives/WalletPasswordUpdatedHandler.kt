package pl.pollub.bsi.domain.archives

import io.micronaut.context.annotation.Context
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.archives.PasswordUpdatedEvent
import pl.pollub.bsi.application.archives.dto.DataChangedEventType
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.archives.api.DataChangeResponse
import pl.pollub.bsi.domain.archives.api.TableName
import pl.pollub.bsi.domain.archives.port.DataChangeRepository

@Context
class WalletPasswordUpdatedHandler(private val dataChangeRepository: DataChangeRepository) : DataChangedHandler<PasswordUpdatedEvent.Data> {
    override fun handle(content: PasswordUpdatedEvent.Data): Either<ErrorResponse, DataChangeResponse> {
        return Option.of(content)
            .map {
                dataChangeRepository.save(
                    DataChange(
                        null,
                        userId = it.userId,
                        modifiedRecordId = it.modifiedRecordId,
                        previousValueOfRecord = it.oldPassword,
                        actionType = DataChangedEventType.PASSWORD_UPDATED,
                        tableName = TableName.PASSWORD
                    )
                )
            }
            .map { it.toResponse() }
            .toEither { ErrorResponse.unexpected() }
    }

    override fun isApplicableFor(eventType: DataChangedEventType): Boolean = eventType == DataChangedEventType.PASSWORD_UPDATED
}
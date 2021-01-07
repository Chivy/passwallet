package pl.pollub.bsi.domain.archives

import io.micronaut.context.annotation.Context
import io.vavr.control.Either
import pl.pollub.bsi.application.archives.dto.DataChangedEventType
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.archives.api.DataChangeResponse
import pl.pollub.bsi.domain.archives.port.DataChangeRepository
import pl.pollub.bsi.application.archives.UserPasswordChangedEvent.Data as UserPasswordChangedEventData

@Context
class UserPasswordChangedHandler(private val dataChangeRepository: DataChangeRepository) : DataChangedHandler<UserPasswordChangedEventData> {
    override fun handle(content: UserPasswordChangedEventData): Either<ErrorResponse, DataChangeResponse> {
        return Either.right<ErrorResponse, UserPasswordChangedEventData>(content)
            .map {
                dataChangeRepository.save(
                    DataChange.userPasswordChanged(it)
                )
            }
            .map { it.toResponse() }

    }

    override fun isApplicableFor(eventType: DataChangedEventType) = eventType == DataChangedEventType.USER_PASSWORD_CHANGED
}
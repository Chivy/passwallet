package pl.pollub.bsi.application.archives

import io.micronaut.context.annotation.Context
import pl.pollub.bsi.application.archives.dto.DataChangedEventType
import pl.pollub.bsi.domain.archives.port.DataChangeRepository
import pl.pollub.bsi.domain.password.port.PasswordRepository
import javax.persistence.EntityNotFoundException

@Context
class RestoreService(
    private val dataChangedRepository: DataChangeRepository,
    private val passwordRepository: PasswordRepository
) {
    fun restore(archiveId: Long) {
        val dataChange = dataChangedRepository.findById(archiveId).getOrElseThrow { EntityNotFoundException() }

        if (dataChange.actionType == DataChangedEventType.PASSWORD_DELETED) {
            passwordRepository.restore(dataChange.modifiedRecordId)
        } else if (dataChange.actionType == DataChangedEventType.PASSWORD_UPDATED) {
            passwordRepository.update(
                passwordId = dataChange.modifiedRecordId,
                password = dataChange.previousValueOfRecord
            )
        }
    }


}

package pl.pollub.bsi.domain.archives.port

import io.vavr.control.Option
import pl.pollub.bsi.domain.archives.DataChange

interface DataChangeRepository {
    fun save(dataChange: DataChange): DataChange
    fun findAllBy(userId: Long): List<DataChange>
    fun findById(archiveId: Long): Option<DataChange>

}

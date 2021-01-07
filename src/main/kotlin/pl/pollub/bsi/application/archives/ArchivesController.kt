package pl.pollub.bsi.application.archives

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.transaction.SynchronousTransactionManager
import pl.pollub.bsi.domain.archives.DataChange
import pl.pollub.bsi.domain.archives.api.FunctionRunFacade
import pl.pollub.bsi.domain.archives.api.FunctionRunResponse
import pl.pollub.bsi.domain.archives.port.DataChangeRepository
import java.sql.Connection

@Controller("/archives")
class ArchivesController(
    private val functionRunFacade: FunctionRunFacade,
    private val dataChangedRepository: DataChangeRepository,
    private val tx: SynchronousTransactionManager<Connection>,
    private val restoreService: RestoreService
) {


    @Get("functions")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    fun findFunctionsRunAllBy(userId: Long): HttpResponse<List<FunctionRunResponse>> = HttpResponse.ok(
        tx.executeRead { functionRunFacade.findAllBy(userId) }
    )

    @Get("data-changes")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    fun findDataChangesBy(userId: Long): HttpResponse<List<DataChange>> = HttpResponse.ok(
        tx.executeRead { dataChangedRepository.findAllBy(userId) }
    )

    @Post("{archiveId}")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    fun restore(@PathVariable archiveId: Long) = HttpResponse.ok(
        tx.executeWrite { restoreService.restore(archiveId) }
    )
}
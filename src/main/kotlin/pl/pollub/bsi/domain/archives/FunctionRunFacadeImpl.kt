package pl.pollub.bsi.domain.archives

import io.micronaut.context.annotation.Context
import pl.pollub.bsi.domain.archives.api.FunctionRunFacade
import pl.pollub.bsi.domain.archives.api.FunctionRunResponse
import pl.pollub.bsi.domain.archives.api.SaveFunctionRunCommand

@Context
internal class FunctionRunFacadeImpl(private val functionRunService: FunctionRunService) : FunctionRunFacade {
    override fun save(saveFunctionRunCommand: SaveFunctionRunCommand) {
        functionRunService.save(saveFunctionRunCommand)
    }

    override fun findAllBy(userId: Long): List<FunctionRunResponse> {
        return functionRunService.findAllBy(userId)
    }
}
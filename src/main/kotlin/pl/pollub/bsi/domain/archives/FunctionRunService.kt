package pl.pollub.bsi.domain.archives

import io.micronaut.context.annotation.Context
import pl.pollub.bsi.domain.archives.api.FunctionRunResponse
import pl.pollub.bsi.domain.archives.api.SaveFunctionRunCommand
import pl.pollub.bsi.domain.archives.port.FunctionRunRepository
import java.util.stream.Collectors

@Context
internal class FunctionRunService(private val functionRunRepository: FunctionRunRepository) {
    fun save(saveFunctionRunCommand: SaveFunctionRunCommand) {
        functionRunRepository.save(
            FunctionRun(
                userId = saveFunctionRunCommand.userId,
                functionName = saveFunctionRunCommand.functionName
            )
        )
    }

    fun findAllBy(userId: Long): List<FunctionRunResponse> {
        return functionRunRepository.findAllByUserId(userId)
            .stream()
            .map { it.toResponse() }
            .collect(Collectors.toList())
    }

}

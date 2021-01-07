package pl.pollub.bsi.domain.archives

import pl.pollub.bsi.domain.archives.api.FunctionName
import pl.pollub.bsi.domain.archives.api.FunctionRunResponse

internal data class FunctionRun(
    val id: Long? = null,
    val userId: Long,
    val functionName: FunctionName
) {
    fun toResponse() = FunctionRunResponse(
        this.id,
        this.userId,
        this.functionName
    )
}

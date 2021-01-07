package pl.pollub.bsi.domain.archives.port

import pl.pollub.bsi.domain.archives.FunctionRun

internal interface FunctionRunRepository {
    fun save(functionRun: FunctionRun)
    fun findAllByUserId(userId: Long): List<FunctionRun>
}

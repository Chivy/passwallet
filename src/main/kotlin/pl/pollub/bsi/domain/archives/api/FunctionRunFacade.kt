package pl.pollub.bsi.domain.archives.api

interface FunctionRunFacade {
    fun save(saveFunctionRunCommand: SaveFunctionRunCommand)
    fun findAllBy(userId: Long): List<FunctionRunResponse>
}

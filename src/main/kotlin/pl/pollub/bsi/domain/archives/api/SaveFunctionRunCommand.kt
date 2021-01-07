package pl.pollub.bsi.domain.archives.api

data class SaveFunctionRunCommand(
    val userId: Long,
    val functionName: FunctionName
)

package pl.pollub.bsi.application.archives

import io.micronaut.context.event.ApplicationEvent
import pl.pollub.bsi.domain.archives.api.FunctionName

class FunctionRunEvent(
    source: Any?,
    val userId: Long,
    val functionName: FunctionName
) : ApplicationEvent(source)

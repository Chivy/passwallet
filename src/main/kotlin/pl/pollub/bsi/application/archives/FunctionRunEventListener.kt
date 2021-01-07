package pl.pollub.bsi.application.archives

import io.micronaut.runtime.event.annotation.EventListener
import pl.pollub.bsi.domain.archives.api.FunctionRunFacade
import pl.pollub.bsi.domain.archives.api.SaveFunctionRunCommand
import javax.inject.Singleton

@Singleton
internal class FunctionRunEventListener(private val functionRunFacade: FunctionRunFacade) {

    @EventListener
    internal fun onFunctionRunEvent(functionRunEvent: FunctionRunEvent) {
        functionRunFacade.save(SaveFunctionRunCommand(functionRunEvent.userId, functionRunEvent.functionName))
    }
}
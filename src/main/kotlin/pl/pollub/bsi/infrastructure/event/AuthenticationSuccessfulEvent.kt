package pl.pollub.bsi.infrastructure.event

import io.micronaut.context.event.ApplicationEvent

class AuthenticationSuccessfulEvent(source: Any?, val userId: Long, val ipAddress: String) : ApplicationEvent(source)

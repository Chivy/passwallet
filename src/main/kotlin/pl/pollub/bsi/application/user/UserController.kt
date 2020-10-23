package pl.pollub.bsi.application.user

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import pl.pollub.bsi.application.user.api.CreateUserApplicationRequest
import pl.pollub.bsi.infrastructure.response.ResponseResolver
import java.security.Principal
import javax.annotation.security.PermitAll
import javax.inject.Inject

@Controller("users")
class UserController(
        @Inject private val userApplicationService: UserApplicationService,
        @Inject private val responseResolver: ResponseResolver
) {

    @PermitAll
    @Post
    fun register(@Body createUserApplicationRequest: CreateUserApplicationRequest): HttpResponse<Any> {
        return responseResolver.resolve(
                userApplicationService.save(createUserApplicationRequest), HttpStatus.CREATED
        )
    }

    @Get("/{userId}")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    fun details(@PathVariable userId: Long,
                @QueryValue passwordsDisclosed: Boolean,
    principal: Principal) : HttpResponse<Any> {

        println(principal.name)
        return responseResolver.resolve(
                userApplicationService.details(userId, passwordsDisclosed), HttpStatus.OK
        )
    }
}
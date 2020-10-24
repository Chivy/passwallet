package pl.pollub.bsi.application.user

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.vavr.control.Option
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

    @Get("{userId}")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    fun details(@PathVariable userId: Long,
                passwordDisclosed: Boolean?,
                principal: Principal): HttpResponse<Any> {

        return responseResolver.resolve(
                userApplicationService.details(
                        userId,
                        principal.name,
                        Option.of(passwordDisclosed).getOrElse(false)!!,
                ), HttpStatus.OK
        )
    }
}
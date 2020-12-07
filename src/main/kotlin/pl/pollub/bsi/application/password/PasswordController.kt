package pl.pollub.bsi.application.password

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED
import pl.pollub.bsi.application.AccessMode
import pl.pollub.bsi.application.password.api.CreatePasswordApplicationRequest
import pl.pollub.bsi.infrastructure.response.ResponseResolver
import java.security.Principal

@Secured(IS_AUTHENTICATED)
@Controller("users/{userId}/passwords")
internal class PasswordController(private val passwordApplicationService: PasswordApplicationService,
                                  private val responseResolver: ResponseResolver) {
    @Post
    fun create(@PathVariable userId: Long,
               @Body passwordCreationApplicationRequest: CreatePasswordApplicationRequest,
               principal: Principal): HttpResponse<Any> {
        return responseResolver.resolve(
                passwordApplicationService.create(userId, principal.name, passwordCreationApplicationRequest),
                HttpStatus.CREATED
        )
    }

    @Get
    fun detailsList(@PathVariable userId: Long,
                    principal: Principal,
                    passwordsDisclosed: Boolean = false): HttpResponse<Any> {
        return responseResolver.resolve(
                passwordApplicationService.detailsList(userId, principal.name, passwordsDisclosed),
                HttpStatus.OK
        )
    }

    @Delete("{passwordId}")
    fun delete(@PathVariable userId: Long,
               @PathVariable passwordId: Long,
               @Header(value = "MODE", defaultValue = "READ") accessMode: AccessMode,
               principal: Principal): HttpResponse<*> {
        return responseResolver.resolve(
                passwordApplicationService.delete(userId, passwordId, principal.name, accessMode),
                HttpStatus.OK
        )
    }

    @Get("{passwordId}")
    fun details(@PathVariable userId: Long,
                @PathVariable passwordId: Long,
                principal: Principal): HttpResponse<Any> {
        return responseResolver.resolve(
                passwordApplicationService.details(userId, principal.name, passwordId),
                HttpStatus.OK
        )
    }
}
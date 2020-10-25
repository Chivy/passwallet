package pl.pollub.bsi.application.password

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import pl.pollub.bsi.application.password.api.CreatePasswordApplicationRequest
import pl.pollub.bsi.infrastructure.response.ResponseResolver
import java.security.Principal

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
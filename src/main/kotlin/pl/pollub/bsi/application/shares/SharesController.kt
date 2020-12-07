package pl.pollub.bsi.application.shares

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED
import pl.pollub.bsi.application.shares.api.CreatePasswordShareApplicationRequest
import pl.pollub.bsi.infrastructure.response.ResponseResolver
import java.security.Principal

@Controller("shares")
internal class SharesController(private val sharesApplicationService: SharesApplicationService,
                                private val responseResolver: ResponseResolver) {

    @Post
    @Secured(IS_AUTHENTICATED)
    fun share(principal: Principal, @Body createPasswordShare: CreatePasswordShareApplicationRequest): HttpResponse<*> {
        return responseResolver.resolve(sharesApplicationService.share(principal.name, createPasswordShare), HttpStatus.CREATED)
    }
}
package pl.pollub.bsi.application.user

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import pl.pollub.bsi.application.api.CreateUserApplicationResponse
import pl.pollub.bsi.application.user.api.CreateUserApplicationRequest
import pl.pollub.bsi.infrastructure.response.ResponseResolver
import javax.inject.Inject

@Controller("users")
class UserController(
        @Inject private val userApplicationService: UserApplicationService,
        @Inject private val responseResolver: ResponseResolver
) {

    @Post
    fun save(createUserApplicationRequest: CreateUserApplicationRequest): HttpResponse<Any> {
        return responseResolver.resolve(
                userApplicationService.save(createUserApplicationRequest), HttpStatus.CREATED
        )
    }

}
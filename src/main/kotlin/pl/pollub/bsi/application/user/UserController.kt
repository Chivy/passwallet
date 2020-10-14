package pl.pollub.bsi.application.user

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import pl.pollub.bsi.application.api.CreateUserApplicationResponse
import pl.pollub.bsi.application.user.api.CreateUserApplicationRequest

@Controller("/users")
class UserController(
        private val userApplicationService: UserApplicationService
) {

    @Post
    fun save(createUserApplicationRequest : CreateUserApplicationRequest) : HttpResponse<CreateUserApplicationResponse> {
        return HttpResponse.created(userApplicationService.save(createUserApplicationRequest))
    }

}
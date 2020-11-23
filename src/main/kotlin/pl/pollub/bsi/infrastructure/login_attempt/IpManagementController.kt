package pl.pollub.bsi.infrastructure.login_attempt

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.QueryValue
import pl.pollub.bsi.infrastructure.login_attempt.api.LoginHandlerFacade
import javax.annotation.security.PermitAll

@Controller("ip-address-blockade")
class IpManagementController(private val loginHandlerFacade: LoginHandlerFacade) {

    @Delete
    @PermitAll
    fun removeIpBlockade(@QueryValue(value = "ipAddress") ipAddress: String?): HttpResponse<Unit> {
        return HttpResponse.ok(loginHandlerFacade.removeIpBlockade(ipAddress))
    }
}
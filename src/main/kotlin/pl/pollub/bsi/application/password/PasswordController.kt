package pl.pollub.bsi.application.password

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED
import pl.pollub.bsi.application.AccessMode
import pl.pollub.bsi.application.password.api.CreatePasswordApplicationRequest
import pl.pollub.bsi.application.password.api.UpdatePasswordApplicationRequest
import pl.pollub.bsi.infrastructure.response.ResponseResolver
import java.security.Principal

@Secured(IS_AUTHENTICATED)
@Controller("users/{userId}/passwords")
internal class PasswordController(
    private val passwordApplicationService: PasswordApplicationService,
    private val responseResolver: ResponseResolver
) {
    @Post
    fun create(
        @PathVariable userId: Long,
        @Body passwordCreationApplicationRequest: CreatePasswordApplicationRequest,
        principal: Principal
    ) = responseResolver.resolve(
        passwordApplicationService.create(userId, principal.name, passwordCreationApplicationRequest),
        HttpStatus.CREATED
    )

    @Put("{passwordId}")
    fun update(
        @PathVariable userId: Long,
        @PathVariable passwordId: Long,
        @Body updatePasswordApplicationRequest: UpdatePasswordApplicationRequest,
        principal: Principal
    ) = responseResolver.resolve(
        passwordApplicationService.update(passwordId, principal.name, updatePasswordApplicationRequest),
        HttpStatus.NO_CONTENT
    )

    @Get
    fun detailsList(
        @PathVariable userId: Long,
        principal: Principal,
        passwordsDisclosed: Boolean = false
    ) = responseResolver.resolve(
        passwordApplicationService.detailsList(userId, principal.name, passwordsDisclosed),
        HttpStatus.OK
    )

    @Delete("{passwordId}")
    fun delete(
        @PathVariable userId: Long,
        @PathVariable passwordId: Long,
        @Header(value = "MODE", defaultValue = "READ") accessMode: AccessMode,
        principal: Principal
    ) = responseResolver.resolve(
        passwordApplicationService.delete(userId, passwordId, principal.name, accessMode),
        HttpStatus.OK
    )

    @Get("{passwordId}")
    fun details(
        @PathVariable userId: Long,
        @PathVariable passwordId: Long,
        principal: Principal
    ) = responseResolver.resolve(
        passwordApplicationService.details(userId, principal.name, passwordId),
        HttpStatus.OK
    )

}
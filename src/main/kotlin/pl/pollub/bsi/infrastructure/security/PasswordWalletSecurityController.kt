package pl.pollub.bsi.infrastructure.security

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken
import javax.annotation.security.PermitAll

@Client("/")
interface PasswordWalletSecurityController {

    @PermitAll
    @Post("login")
    fun login(@Body credentials: UsernamePasswordCredentials) : BearerAccessRefreshToken
}
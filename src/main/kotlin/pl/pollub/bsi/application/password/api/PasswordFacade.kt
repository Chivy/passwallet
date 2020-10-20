package pl.pollub.bsi.application.password.api

import pl.pollub.bsi.domain.password.Password
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.api.PasswordResponse

interface PasswordFacade {
    fun create(password: PasswordCreationCommand) : PasswordResponse

}

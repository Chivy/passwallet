package pl.pollub.bsi.application.password

import io.micronaut.context.annotation.Context
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.password.Password
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.password.port.PasswordRepository
import javax.inject.Inject

@Context
internal class PasswordService(@Inject private val passwordRepository: PasswordRepository) {
    fun create(passwordCreationCommand: PasswordCreationCommand): Either<ErrorResponse, Password> {
        return Option.of(passwordCreationCommand)
                .map {  }
    }

}

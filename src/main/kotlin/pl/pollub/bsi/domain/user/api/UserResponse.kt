package pl.pollub.bsi.domain.user.api

import io.vavr.collection.List
import pl.pollub.bsi.domain.api.Algorithm
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand

data class UserResponse(val login: String, val password: String, val algorithm: Algorithm, val passwords: List<PasswordCreationCommand>)
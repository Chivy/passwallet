package pl.pollub.bsi.domain.password.port

import pl.pollub.bsi.domain.password.Password

internal interface PasswordRepository {
    fun save(password: Password, userId: Long): Password

}
package pl.pollub.bsi.domain.user.port

import pl.pollub.bsi.domain.user.Password

interface PasswordRepository {
    fun save(password: Password, userId: Long): Password

}

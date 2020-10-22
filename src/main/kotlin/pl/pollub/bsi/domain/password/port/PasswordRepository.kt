package pl.pollub.bsi.domain.password.port

import io.vavr.collection.List
import pl.pollub.bsi.domain.password.Password

internal interface PasswordRepository {
    fun save(userId: Long, password: Password): Password
    fun findByUserId(userId: Long): List<Password>
}
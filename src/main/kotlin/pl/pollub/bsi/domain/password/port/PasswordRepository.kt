package pl.pollub.bsi.domain.password.port

import io.vavr.collection.List
import io.vavr.control.Option
import pl.pollub.bsi.domain.password.Password
import pl.pollub.bsi.domain.password.PasswordId

interface PasswordRepository {
    fun save(userId: Long, password: Password): Password
    fun findByUserId(userId: Long): List<Password>
    fun update(passwordId: PasswordId, password: String): Password
    fun update(passwordId: Long, password: String): Password
    fun findById(passwordId: Long): Option<Password>
    fun deleteByPasswordId(passwordId: Long): Long
    fun restore(modifiedRecordId: Long)
}
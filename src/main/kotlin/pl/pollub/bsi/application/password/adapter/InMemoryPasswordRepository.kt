package pl.pollub.bsi.application.password.adapter

import io.vavr.collection.List
import io.vavr.collection.Stream
import io.vavr.control.Option
import io.vavr.kotlin.toVavrList
import pl.pollub.bsi.domain.password.Password
import pl.pollub.bsi.domain.password.PasswordId
import pl.pollub.bsi.domain.password.port.PasswordRepository
import kotlin.random.Random

internal class InMemoryPasswordRepository : PasswordRepository {
    private val data = HashMap<Long, Password>()

    override fun save(userId: Long, password: Password): Password {
        val passwordWithId = Password(
                PasswordId(
                        Random.nextLong(),
                        userId
                ),
                password.login,
                password.password,
                password.masterPassword,
                password.webAddress,
                password.description
        )
        data[passwordWithId.passwordId.id] = passwordWithId
        return passwordWithId
    }

    override fun findByUserId(userId: Long): List<Password> {
        return Stream.ofAll(data.values)
                .filter { it.passwordId.userId == userId }
                .toVavrList()
    }

    override fun update(passwordId: PasswordId, password: String): Password {
        return findById(passwordId.id)
                .map { it.withPassword(password) }
                .peek { data[passwordId.id] = it }
                .getOrElseThrow { RuntimeException() }
    }

    override fun findById(passwordId: Long): Option<Password> {
        return Stream.ofAll(data.values)
                .find { it.passwordId.id == passwordId }
    }
}
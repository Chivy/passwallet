package pl.pollub.bsi.infrastructure.login_attempt

import io.vavr.collection.List
import io.vavr.collection.Stream
import io.vavr.control.Option
import io.vavr.kotlin.toVavrList
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class InMemoryLoginAttemptRepository : LoginAttemptRepository {
    private val data = ConcurrentHashMap<Long, LoginAttempt>()

    override fun save(loginAttempt: LoginAttempt) {
        val loginAttemptWithId = LoginAttempt(
                Random.nextLong(),
                loginAttempt.dateCreated,
                loginAttempt.userId,
                loginAttempt.isSuccessful,
                loginAttempt.blockedUntil
        )
        loginAttemptWithId.id?.let { data.put(it, loginAttemptWithId) }
    }

    override fun findByUserId(userId: Long): List<LoginAttempt> {
        return Stream.ofAll(data.entries)
                .map { it.value }
                .filter { userId == it.userId }
                .toVavrList()
    }

    override fun findLastFailureByUserId(userId: Long): Option<LoginAttempt> {
        return Stream.ofAll(data.entries)
                .map { it.value }
                .maxBy { it, next -> it.dateCreated.compareTo(next.dateCreated) }
    }

    override fun deleteByUserId(userId: Long) {
        data.remove(
                Stream.ofAll(data.entries)
                        .find { it.value.userId == userId }
                        .map { it.value }
                        .map { it.id }
                        .getOrElse(0L)
        )
    }
}
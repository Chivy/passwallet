package pl.pollub.bsi.application.user.adapter

import io.vavr.collection.Stream
import io.vavr.control.Option
import pl.pollub.bsi.domain.user.User
import pl.pollub.bsi.domain.user.port.UserRepository
import java.util.*
import kotlin.collections.HashMap

internal class InMemoryUserRepository : UserRepository {
    private val data = HashMap<Long, User>()

    override fun save(user: User): User {
        val userWithId = User(
                Random().nextLong(),
                user.login,
                user.password,
                user.algorithm,
                user.salt,
                user.isPasswordHashed,
                user.passwords
        )
        data[userWithId.id] = userWithId
        return userWithId
    }

    override fun existsByLogin(login: String): Boolean {
        return findByLogin(login).isDefined
    }

    override fun findByLogin(username: String): Option<User> {
        return Stream.ofAll(data.values)
                .find { username == it.login }
    }

    override fun findById(userId: Long): Option<User> {
        return Stream.ofAll(data.values)
                .find { userId == it.id }
    }

    override fun update(user: User): User {
        return findById(user.id)
                .peek { data[user.id] = user }
                .map { data[user.id]!! }
                .getOrElseThrow { RuntimeException() }
    }
}
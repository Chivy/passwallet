package pl.pollub.bsi.domain.user.port

import io.vavr.control.Option
import pl.pollub.bsi.domain.user.User
import pl.pollub.bsi.domain.user.api.UserResponse

internal interface UserRepository {
    fun save(user: User) : User
    fun existsByLogin(login: String): Boolean
    fun findByLogin(username: String): Option<User>
    fun findById(userId: Long): Option<User>
    fun update(user: User): User

}

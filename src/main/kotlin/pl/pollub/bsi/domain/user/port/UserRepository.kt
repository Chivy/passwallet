package pl.pollub.bsi.domain.user.port

import pl.pollub.bsi.domain.user.User
import pl.pollub.bsi.domain.user.api.UserResponse

interface UserRepository {
    fun save(user: User) : User
    fun existsByLogin(login: String): Boolean

}

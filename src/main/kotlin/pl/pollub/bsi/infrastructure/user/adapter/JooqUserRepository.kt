package pl.pollub.bsi.infrastructure.user.adapter

import pl.pollub.bsi.domain.user.User
import pl.pollub.bsi.domain.user.port.UserRepository

class JooqUserRepository : UserRepository {
    override fun save(user: User): User {
        TODO("Not yet implemented")
    }
}
package pl.pollub.bsi.adapter.user

import nu.studer.sample.tables.User.USER
import org.jooq.DSLContext
import pl.pollub.bsi.domain.user.User
import pl.pollub.bsi.domain.user.port.UserRepository
import javax.inject.Singleton

@Singleton
class JooqUserRepository(
        private val dslContext: DSLContext
) : UserRepository {
    override fun save(user: User): User {
        return dslContext.insertInto(USER)
                .columns(USER.LOGIN,
                        USER.PASSWORD_HASH,
                        USER.SALT,
                        USER.IS_PASSWORD_KEPT_AS_HASH
                )
                .values(user.login,
                        user.password,
                        user.salt,
                        user.isPasswordHashed
                )
                .returningResult(
                        USER.ID,
                        USER.LOGIN,
                        USER.PASSWORD_HASH,
                        USER.SALT,
                        USER.IS_PASSWORD_KEPT_AS_HASH
                )
                .fetchOne()
                .map {
                    User(
                            it.getValue(USER.ID),
                            it.getValue(USER.LOGIN),
                            it.getValue(USER.PASSWORD_HASH),
                            user.algorithm,
                            it.getValue(USER.SALT),
                            it.getValue(USER.IS_PASSWORD_KEPT_AS_HASH),
                            user.passwords
                    )
                }
    }

    override fun existsByLogin(login: String): Boolean {
        return dslContext.selectFrom(USER)
                .where(USER.LOGIN.eq(login))
                .fetchOptional()
                .isPresent
    }
}
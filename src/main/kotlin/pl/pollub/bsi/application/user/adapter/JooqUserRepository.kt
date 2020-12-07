package pl.pollub.bsi.application.user.adapter

import io.vavr.collection.List
import io.vavr.control.Option
import nu.studer.sample.Tables.USER
import org.jooq.DSLContext
import org.jooq.impl.DSL.row
import pl.pollub.bsi.domain.user.User
import pl.pollub.bsi.domain.user.api.Algorithm
import pl.pollub.bsi.domain.user.port.UserRepository
import javax.inject.Singleton

@Singleton
internal class JooqUserRepository(
        private val dslContext: DSLContext
) : UserRepository {
    override fun save(user: User): User {
        return dslContext.insertInto(USER)
                .columns(
                        USER.LOGIN,
                        USER.PASSWORD_HASH,
                        USER.ALGORITHM,
                        USER.SALT,
                        USER.IS_PASSWORD_KEPT_AS_HASH
                )
                .values(
                        user.login,
                        user.password,
                        user.algorithm.instance,
                        user.salt,
                        user.isPasswordHashed
                )
                .returningResult(
                        USER.ID,
                        USER.LOGIN,
                        USER.ALGORITHM,
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
                            Algorithm.resolve(it.getValue(USER.ALGORITHM)),
                            it.getValue(USER.SALT),
                            it.getValue(USER.IS_PASSWORD_KEPT_AS_HASH),
                            user.passwords
                    )
                }
    }

    override fun update(user: User): User {
        return dslContext.update(USER)
                .set(
                        row(
                                USER.ALGORITHM,
                                USER.SALT,
                                USER.PASSWORD_HASH,
                                USER.IS_PASSWORD_KEPT_AS_HASH
                        ),
                        row(
                                user.algorithm.instance,
                                user.salt,
                                user.password,
                                user.isPasswordHashed
                        )
                )
                .where(USER.ID.eq(user.id))
                .returningResult(
                        USER.ID,
                        USER.LOGIN,
                        USER.ALGORITHM,
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
                            Algorithm.resolve(it.getValue(USER.ALGORITHM)),
                            it.getValue(USER.SALT),
                            it.getValue(USER.IS_PASSWORD_KEPT_AS_HASH),
                            user.passwords
                    )
                }


    }

    override fun existsByLogin(login: String): Boolean {
        return findByLogin(login).isDefined
    }

    override fun findByLogin(username: String): Option<User> {
        return Option.ofOptional(dslContext.selectFrom(USER)
                .where(USER.LOGIN.eq(username))
                .fetchOptional()
                .map {
                    User(
                            it.getValue(USER.ID),
                            it.getValue(USER.LOGIN),
                            it.getValue(USER.PASSWORD_HASH),
                            Algorithm.resolve(it.getValue(USER.ALGORITHM)),
                            it.getValue(USER.SALT),
                            it.getValue(USER.IS_PASSWORD_KEPT_AS_HASH),
                            List.empty()
                    )
                }
        )
    }

    override fun findById(userId: Long): Option<User> {
        return Option.ofOptional(
                dslContext.selectFrom(USER)
                        .where(USER.ID.eq(userId))
                        .fetchOptional()
                        .map {
                            User(
                                    it.getValue(USER.ID),
                                    it.getValue(USER.LOGIN),
                                    it.getValue(USER.PASSWORD_HASH),
                                    Algorithm.resolve(it.getValue(USER.ALGORITHM)),
                                    it.getValue(USER.SALT),
                                    it.getValue(USER.IS_PASSWORD_KEPT_AS_HASH),
                                    List.empty()
                            )
                        }
        )
    }
}
package pl.pollub.bsi.application.password.adapter

import io.vavr.collection.List
import io.vavr.control.Option
import io.vavr.kotlin.toVavrList
import io.vavr.kotlin.toVavrStream
import nu.studer.sample.Sequences
import nu.studer.sample.Tables.PASSWORDS
import org.jooq.DSLContext
import org.jooq.impl.DSL.row
import pl.pollub.bsi.domain.password.Password
import pl.pollub.bsi.domain.password.PasswordId
import pl.pollub.bsi.domain.password.port.PasswordRepository
import javax.inject.Singleton

@Singleton
internal class JooqPasswordRepository(
        private val dslContext: DSLContext
) : PasswordRepository {
    override fun save(userId: Long, password: Password): Password {
        return dslContext.insertInto(PASSWORDS)
                .columns(
                        PASSWORDS.ID,
                        PASSWORDS.LOGIN,
                        PASSWORDS.PASSWORD,
                        PASSWORDS.WEB_ADDRESS,
                        PASSWORDS.DESCRIPTION,
                        PASSWORDS.USER_ID
                )
                .values(
                        dslContext.nextval(Sequences.PASSWORD_SEQ),
                        password.login,
                        password.password,
                        password.webAddress,
                        password.description,
                        userId
                )
                .returning(
                        PASSWORDS.ID,
                        PASSWORDS.USER_ID,
                        PASSWORDS.LOGIN,
                        PASSWORDS.PASSWORD,
                        PASSWORDS.WEB_ADDRESS,
                        PASSWORDS.DESCRIPTION
                )
                .fetchOne()
                .map {
                    Password(
                            PasswordId(
                                    it.getValue(PASSWORDS.ID),
                                    it.getValue(PASSWORDS.USER_ID)
                            ),
                            it.getValue(PASSWORDS.LOGIN),
                            it.getValue(PASSWORDS.PASSWORD),
                            password.masterPassword,
                            it.getValue(PASSWORDS.WEB_ADDRESS),
                            it.getValue(PASSWORDS.DESCRIPTION)
                    )
                }
    }

    override fun update(passwordId: PasswordId, password: String): Password {
        return dslContext.update(PASSWORDS)
                .set(
                        row(PASSWORDS.PASSWORD),
                        row(password)
                )
                .where(PASSWORDS.ID.eq(passwordId.id))
                .and(PASSWORDS.USER_ID.eq(passwordId.userId))
                .returning(
                        PASSWORDS.ID,
                        PASSWORDS.USER_ID,
                        PASSWORDS.LOGIN,
                        PASSWORDS.PASSWORD,
                        PASSWORDS.WEB_ADDRESS,
                        PASSWORDS.DESCRIPTION
                )
                .fetchOne()
                .map {
                    Password(
                            PasswordId(
                                    it.getValue(PASSWORDS.ID),
                                    it.getValue(PASSWORDS.USER_ID)
                            ),
                            it.getValue(PASSWORDS.LOGIN),
                            it.getValue(PASSWORDS.PASSWORD),
                            null,
                            it.getValue(PASSWORDS.WEB_ADDRESS),
                            it.getValue(PASSWORDS.DESCRIPTION)
                    )
                }

    }

    override fun findById(passwordId: Long): Option<Password> {
        return Option.of(
                dslContext.selectFrom(PASSWORDS)
                        .where(PASSWORDS.ID.eq(passwordId))
                        .fetchOne()
                        .map {
                            Password(
                                    PasswordId(
                                            it.getValue(PASSWORDS.ID),
                                            it.getValue(PASSWORDS.USER_ID)
                                    ),
                                    it.getValue(PASSWORDS.LOGIN),
                                    it.getValue(PASSWORDS.PASSWORD),
                                    null,
                                    it.getValue(PASSWORDS.WEB_ADDRESS),
                                    it.getValue(PASSWORDS.DESCRIPTION)
                            )
                        }
        )
    }

    override fun findByUserId(userId: Long): List<Password> {
        return dslContext.selectFrom(PASSWORDS)
                .where(PASSWORDS.USER_ID.eq(userId))
                .fetch()
                .toVavrStream()
                .map {
                    Password(
                            PasswordId(
                                    it.getValue(PASSWORDS.ID),
                                    it.getValue(PASSWORDS.USER_ID)
                            ),
                            it.getValue(PASSWORDS.LOGIN),
                            it.getValue(PASSWORDS.PASSWORD),
                            null,
                            it.getValue(PASSWORDS.WEB_ADDRESS),
                            it.getValue(PASSWORDS.DESCRIPTION)
                    )
                }.toVavrList()
    }
}
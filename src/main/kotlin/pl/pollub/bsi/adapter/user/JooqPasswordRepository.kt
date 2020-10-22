package pl.pollub.bsi.adapter.user

import io.vavr.collection.List
import io.vavr.kotlin.toVavrList
import io.vavr.kotlin.toVavrStream
import nu.studer.sample.Sequences
import nu.studer.sample.Tables.PASSWORDS
import org.jooq.DSLContext
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
                            it.getValue(PASSWORDS.WEB_ADDRESS),
                            it.getValue(PASSWORDS.DESCRIPTION)
                    )
                }
    }

    override fun findByUserId(userId: Long): List<Password> {
        return dslContext.selectFrom(PASSWORDS)
                .where(PASSWORDS.USER_ID.eq(userId))
                .fetch()
                .toVavrStream()
                .map {
                    Password (
                            PasswordId(
                                    it.getValue(PASSWORDS.ID),
                                    it.getValue(PASSWORDS.USER_ID)
                            ),
                            it.getValue(PASSWORDS.LOGIN),
                            it.getValue(PASSWORDS.PASSWORD),
                            it.getValue(PASSWORDS.WEB_ADDRESS),
                            it.getValue(PASSWORDS.DESCRIPTION)
                    )
                }.toVavrList()
    }
}
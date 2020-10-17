package pl.pollub.bsi.adapter.user

import nu.studer.sample.tables.Passwords.PASSWORDS
import org.jooq.DSLContext
import pl.pollub.bsi.domain.user.Password
import pl.pollub.bsi.domain.user.port.PasswordRepository
import javax.inject.Singleton

@Singleton
class JooqPasswordRepository(
        private val dslContext: DSLContext
) : PasswordRepository {
    override fun save(password: Password, userId: Long): Password {
        return dslContext.insertInto(PASSWORDS)
                .columns(
                        PASSWORDS.LOGIN,
                        PASSWORDS.PASSWORD,
                        PASSWORDS.WEB_ADDRESS,
                        PASSWORDS.DESCRIPTION,
                        PASSWORDS.USER_ID
                )
                .values(
                        password.login,
                        password.password,
                        password.webAddress,
                        password.description,
                        userId
                )
                .returning(
                        PASSWORDS.LOGIN,
                        PASSWORDS.PASSWORD,
                        PASSWORDS.WEB_ADDRESS,
                        PASSWORDS.DESCRIPTION
                )
                .fetchOne()
                .map {
                    Password(
                            it.getValue(PASSWORDS.LOGIN),
                            it.getValue(PASSWORDS.PASSWORD),
                            it.getValue (PASSWORDS.WEB_ADDRESS),
                            it.getValue (PASSWORDS.DESCRIPTION)
                    )
                }
    }
}
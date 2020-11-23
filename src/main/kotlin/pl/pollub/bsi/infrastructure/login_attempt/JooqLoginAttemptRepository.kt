package pl.pollub.bsi.infrastructure.login_attempt

import io.micronaut.context.annotation.Context
import io.vavr.collection.List
import io.vavr.control.Option
import nu.studer.sample.Tables
import nu.studer.sample.tables.LoginAttempt.LOGIN_ATTEMPT
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.max
import kotlin.streams.toList

@Context
class JooqLoginAttemptRepository(private val dslContext: DSLContext) : LoginAttemptRepository {
    override fun save(loginAttempt: LoginAttempt) {
        return dslContext.insertInto(LOGIN_ATTEMPT)
                .columns(
                        field(LOGIN_ATTEMPT.USER_ID),
                        field(LOGIN_ATTEMPT.IS_SUCCESSFUL),
                        field(LOGIN_ATTEMPT.BLOCKED_UNTIL)
                )
                .values(
                        loginAttempt.userId,
                        loginAttempt.isSuccessful,
                        loginAttempt.blockedUntil
                )
                .returningResult(
                        LOGIN_ATTEMPT.ID,
                        LOGIN_ATTEMPT.DATE_CREATED,
                        LOGIN_ATTEMPT.USER_ID,
                        LOGIN_ATTEMPT.IS_SUCCESSFUL,
                        LOGIN_ATTEMPT.BLOCKED_UNTIL
                )
                .fetchOne()
                .map {
                    LoginAttempt(
                            it.getValue(LOGIN_ATTEMPT.ID),
                            it.getValue(LOGIN_ATTEMPT.DATE_CREATED),
                            it.getValue(LOGIN_ATTEMPT.USER_ID),
                            it.getValue(LOGIN_ATTEMPT.IS_SUCCESSFUL),
                            it.getValue(LOGIN_ATTEMPT.BLOCKED_UNTIL)
                    )
                }
    }

    override fun findByUserId(userId: Long): List<LoginAttempt> {
        return List.ofAll(
                dslContext.selectFrom(Tables.LOGIN_ATTEMPT)
                        .where(LOGIN_ATTEMPT.USER_ID.eq(userId))
                        .orderBy(LOGIN_ATTEMPT.DATE_CREATED)
                        .fetch()
                        .stream()
                        .map {
                            LoginAttempt(
                                    it.getValue(LOGIN_ATTEMPT.ID),
                                    it.getValue(LOGIN_ATTEMPT.DATE_CREATED),
                                    it.getValue(LOGIN_ATTEMPT.USER_ID),
                                    it.getValue(LOGIN_ATTEMPT.IS_SUCCESSFUL),
                                    it.getValue(LOGIN_ATTEMPT.BLOCKED_UNTIL)
                            )
                        }
                        .toList()
        )

    }

    override fun findLastFailureByUserId(userId: Long): Option<LoginAttempt> {
        return Option.ofOptional(
                dslContext.selectFrom(LOGIN_ATTEMPT)
                        .where(LOGIN_ATTEMPT.USER_ID.eq(userId))
                        .and(LOGIN_ATTEMPT.IS_SUCCESSFUL.eq(false))
                        .and(LOGIN_ATTEMPT.DATE_CREATED.eq(
                                dslContext.select(max(LOGIN_ATTEMPT.DATE_CREATED))
                                        .from(LOGIN_ATTEMPT)
                                        .where(LOGIN_ATTEMPT.USER_ID.eq(userId))
                        ))
                        .fetchOptional()
                        .map {
                            LoginAttempt(
                                    it.getValue(LOGIN_ATTEMPT.ID),
                                    it.getValue(LOGIN_ATTEMPT.DATE_CREATED),
                                    it.getValue(LOGIN_ATTEMPT.USER_ID),
                                    it.getValue(LOGIN_ATTEMPT.IS_SUCCESSFUL),
                                    it.getValue(LOGIN_ATTEMPT.BLOCKED_UNTIL)
                            )
                        }
        )
    }

    override fun deleteByUserId(userId: Long) {
        dslContext.deleteFrom(LOGIN_ATTEMPT)
                .where(LOGIN_ATTEMPT.USER_ID.eq(userId))
                .execute()
    }

}

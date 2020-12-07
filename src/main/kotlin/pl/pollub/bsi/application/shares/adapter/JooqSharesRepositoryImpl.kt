package pl.pollub.bsi.application.shares.adapter

import io.vavr.collection.List
import io.vavr.kotlin.toVavrList
import nu.studer.sample.Tables.PASSWORD_SHARE
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import pl.pollub.bsi.domain.shares.PasswordShare
import pl.pollub.bsi.domain.shares.port.SharesRepository
import javax.inject.Singleton

@Singleton
internal class JooqSharesRepositoryImpl(private val dslContext: DSLContext) : SharesRepository {

    override fun save(passwordShare: PasswordShare): PasswordShare {
        return dslContext.insertInto(PASSWORD_SHARE)
                .columns(
                        field(PASSWORD_SHARE.USER_ID),
                        field(PASSWORD_SHARE.PASSWORD_ID)
                )
                .values(
                        passwordShare.userId,
                        passwordShare.passwordId
                )
                .returningResult(
                        PASSWORD_SHARE.ID,
                        PASSWORD_SHARE.USER_ID,
                        PASSWORD_SHARE.PASSWORD_ID
                )
                .fetchOne()
                .map {
                    PasswordShare(
                            it.getValue(PASSWORD_SHARE.ID),
                            it.getValue(PASSWORD_SHARE.USER_ID),
                            it.getValue(PASSWORD_SHARE.PASSWORD_ID)
                    )
                }
    }

    override fun findByUserId(userId: Long): List<PasswordShare> {
        return dslContext.selectFrom(PASSWORD_SHARE)
                .where(PASSWORD_SHARE.USER_ID.eq(userId))
                .fetch()
                .map {
                    PasswordShare(
                            it.getValue(PASSWORD_SHARE.ID),
                            it.getValue(PASSWORD_SHARE.USER_ID),
                            it.getValue(PASSWORD_SHARE.PASSWORD_ID)
                    )
                }
                .toVavrList()
    }

    override fun deleteByPasswordId(passwordId: Long): List<Long> {
        return dslContext.deleteFrom(PASSWORD_SHARE)
                .where(PASSWORD_SHARE.PASSWORD_ID.eq(passwordId))
                .returningResult(PASSWORD_SHARE.ID)
                .fetch()
                .map { it.getValue(PASSWORD_SHARE.ID) }
                .toVavrList()
    }
}
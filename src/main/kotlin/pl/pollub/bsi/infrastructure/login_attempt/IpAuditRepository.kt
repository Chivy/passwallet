package pl.pollub.bsi.infrastructure.login_attempt

import io.micronaut.context.annotation.Context
import io.vavr.collection.List
import io.vavr.control.Option
import nu.studer.sample.Tables
import nu.studer.sample.tables.IpAudit.IP_AUDIT
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import kotlin.streams.toList

@Context
class IpAuditRepository(private val dslContext: DSLContext) {
    fun save(ipAudit: IpAudit) {
        return dslContext.insertInto(IP_AUDIT)
                .columns(
                        field(IP_AUDIT.IP_ADDRESS),
                        field(IP_AUDIT.BLOCKED_UNTIL),
                        field(IP_AUDIT.IS_SUCCESSFUL)
                )
                .values(
                        ipAudit.ipAddress,
                        ipAudit.blockedUntil,
                        ipAudit.isSuccessful
                )
                .returningResult(
                        IP_AUDIT.ID,
                        IP_AUDIT.DATE_CREATED,
                        IP_AUDIT.IP_ADDRESS,
                        IP_AUDIT.BLOCKED_UNTIL,
                        IP_AUDIT.IS_SUCCESSFUL
                )
                .fetchOne()
                .map {
                    IpAudit(
                            it.getValue(IP_AUDIT.ID),
                            it.getValue(IP_AUDIT.DATE_CREATED),
                            it.getValue(IP_AUDIT.IP_ADDRESS),
                            it.getValue(IP_AUDIT.BLOCKED_UNTIL),
                            it.getValue(IP_AUDIT.IS_SUCCESSFUL)
                    )
                }
    }

    fun findByIp(ipAddress: String): List<IpAudit> {
        return List.ofAll(
                dslContext.selectFrom(Tables.IP_AUDIT)
                        .where(IP_AUDIT.IP_ADDRESS.eq(ipAddress))
                        .fetch()
                        .stream()
                        .map {
                            IpAudit(
                                    it.getValue(IP_AUDIT.ID),
                                    it.getValue(IP_AUDIT.DATE_CREATED),
                                    it.getValue(IP_AUDIT.IP_ADDRESS),
                                    it.getValue(IP_AUDIT.BLOCKED_UNTIL),
                                    it.getValue(IP_AUDIT.IS_SUCCESSFUL)
                            )
                        }
                        .toList()
        )
    }

    fun findLastFailureByIp(ipAddress: String): Option<IpAudit> {
        return Option.ofOptional(
                dslContext.selectFrom(IP_AUDIT)
                        .where(IP_AUDIT.IP_ADDRESS.eq(ipAddress))
                        .and(IP_AUDIT.IS_SUCCESSFUL.eq(false))
                        .and(IP_AUDIT.DATE_CREATED.eq(
                                dslContext.select(DSL.max(IP_AUDIT.DATE_CREATED))
                                        .from(IP_AUDIT)
                                        .where(IP_AUDIT.IP_ADDRESS.eq(ipAddress))
                        ))
                        .fetchOptional()
                        .map {
                            IpAudit(
                                    it.getValue(IP_AUDIT.ID),
                                    it.getValue(IP_AUDIT.DATE_CREATED),
                                    it.getValue(IP_AUDIT.IP_ADDRESS),
                                    it.getValue(IP_AUDIT.BLOCKED_UNTIL),
                                    it.getValue(IP_AUDIT.IS_SUCCESSFUL)
                            )
                        }
        )
    }

    fun deleteByIpAddress(ipAddress: String) {
        dslContext.deleteFrom(IP_AUDIT)
                .where(IP_AUDIT.IP_ADDRESS.eq(ipAddress))
                .execute()
    }

}

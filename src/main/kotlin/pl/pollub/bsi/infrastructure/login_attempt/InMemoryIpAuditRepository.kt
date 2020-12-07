package pl.pollub.bsi.infrastructure.login_attempt

import io.vavr.collection.List
import io.vavr.collection.Stream
import io.vavr.control.Option
import io.vavr.kotlin.toVavrList
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class InMemoryIpAuditRepository : IpAuditRepository {
    private val data = ConcurrentHashMap<Long, IpAudit>()

    override fun save(ipAudit: IpAudit) {
        val ipAuditWithId = IpAudit(
                Random.nextLong(),
                ipAudit.dateCreated,
                ipAudit.ipAddress,
                ipAudit.blockedUntil,
                ipAudit.isSuccessful
        )
        ipAuditWithId.id?.let { data.put(it, ipAuditWithId) }
    }

    override fun findByIp(ipAddress: String): List<IpAudit> {
        return Stream.ofAll(data.entries)
                .map { it.value }
                .filter { ipAddress == it.ipAddress }
                .toVavrList()
    }

    override fun findLastFailureByIp(ipAddress: String): Option<IpAudit> {
        return Stream.ofAll(data.entries)
                .map { it.value }
                .maxBy { it, next -> it.dateCreated.compareTo(next.dateCreated) }
    }

    override fun deleteByIpAddress(ipAddress: String) {
        data.remove(
                Stream.ofAll(data.entries)
                        .filter { it.value.ipAddress == ipAddress }
                        .map { it.value.id }
                        .getOrElse(0L)
        )
    }
}
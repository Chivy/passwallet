package pl.pollub.bsi.infrastructure.login_attempt

import io.vavr.collection.List
import io.vavr.control.Option

interface IpAuditRepository {

    fun save(ipAudit: IpAudit)
    fun findByIp(ipAddress: String): List<IpAudit>
    fun findLastFailureByIp(ipAddress: String): Option<IpAudit>
    fun deleteByIpAddress(ipAddress: String)
}

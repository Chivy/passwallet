package pl.pollub.bsi.infrastructure.login_attempt

import java.time.LocalDateTime

class IpAudit(val id: Long?,
              val dateCreated: LocalDateTime,
              val ipAddress: String,
              val blockedUntil: LocalDateTime?,
              val isSuccessful: Boolean)
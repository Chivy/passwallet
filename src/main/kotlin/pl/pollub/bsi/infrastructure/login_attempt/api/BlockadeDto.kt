package pl.pollub.bsi.infrastructure.login_attempt.api

import java.time.LocalDateTime

class BlockadeDto(val accountBlockedTo: LocalDateTime?,
                  val ipBlockedTo: LocalDateTime?)

package pl.pollub.bsi.domain.shares.port

import io.vavr.collection.List
import pl.pollub.bsi.domain.shares.PasswordShare

internal interface SharesRepository {
    fun save(passwordShare: PasswordShare): PasswordShare
    fun findByUserId(userId: Long): List<PasswordShare>
    fun deleteByPasswordId(passwordId: Long): List<Long>
}

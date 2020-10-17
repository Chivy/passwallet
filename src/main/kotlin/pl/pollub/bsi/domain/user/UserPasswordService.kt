package pl.pollub.bsi.domain.user

import io.micronaut.context.annotation.Context
import pl.pollub.bsi.domain.api.Algorithm
import pl.pollub.bsi.domain.password.api.PasswordCreationCommand
import pl.pollub.bsi.domain.user.api.UserCreationCommand
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

@Context
internal class UserPasswordService {
    fun createHashed(user: UserCreationCommand): UserCreationCommand {
        return UserCreationCommand(
                user.login,
                getDigest(user.algorithm, user.password),
                user.algorithm,
                UUID.randomUUID().toString(),
                user.passwords
                        .toStream()
                        .map {
                            PasswordCreationCommand(
                                    it.login,
                                    getDigest(user.algorithm, it.password),
                                    it.webAddress,
                                    it.description
                            )
                        }.toList()
        )
    }

    private fun getDigest(algorithm: Algorithm, password: String): String {
        val messageDigest: MessageDigest = MessageDigest.getInstance(algorithm.instance)
        messageDigest.update(password.toByteArray(StandardCharsets.UTF_8))
        return String.format("%064x", BigInteger(1, messageDigest.digest()))

    }

}

package pl.pollub.bsi.domain.password.api

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class Hasher {

    fun encrypt(algorithm: String, password: String): String {
        return when (algorithm) {
            "SHA-512" -> SHA512.encrypt(password)
            "HMAC" -> HMAC.encrypt(password)
            else -> AES.encrypt(password)
        }
    }

    class SHA512 {
        companion object {
            fun encrypt(password: String): String {
                val messageDigest: MessageDigest = MessageDigest.getInstance("SHA-512")
                messageDigest.update(password.toByteArray(StandardCharsets.UTF_8))
                return String.format("%064x", BigInteger(1, messageDigest.digest()))
            }
        }
    }

    class HMAC {
        companion object {
            fun encrypt(password: String): String {

                return ""
            }
        }
    }

    class AES {
        companion object {
            fun encrypt(password: String): String {
                return ""
            }
        }
    }
}
package pl.pollub.bsi.domain.password.api

import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.xml.crypto.dsig.SignatureMethod;

internal class Encrypter {

    fun encrypt(algorithm: String, password: String, key: String?): String {
        return when (algorithm) {
            "SHA-512" -> SHA512.encrypt(password, key)
            "HMAC" -> HMAC.encrypt(password)
            else -> AES.encrypt(password, key)
        }
    }

    class SHA512 {
        companion object {
            private const val pepper = "a5354ff8"
            fun encrypt(password: String, salt: String?): String {
                val messageDigest: MessageDigest = MessageDigest.getInstance("SHA-512")
                messageDigest.update((password + salt + pepper).toByteArray(StandardCharsets.UTF_8))
                return String.format("%064x", BigInteger(1, messageDigest.digest()))
            }
        }
    }

    class HMAC {
        companion object {
            private const val key = "47733c1c-c969-4d81-af7b-92a5db6a3325";
            fun encrypt(password: String): String {
                val byteKey = key.toByteArray(StandardCharsets.UTF_8)
                val sha512Hmac = Mac.getInstance(SignatureMethod.HMAC_SHA512)
                val keySpec = SecretKeySpec(byteKey, SignatureMethod.HMAC_SHA512)
                sha512Hmac.init(keySpec)
                val macdata = sha512Hmac.doFinal(password.toByteArray(StandardCharsets.UTF_8))
                return Base64.getEncoder().encodeToString(macdata)
            }
        }
    }

    class AES {
        companion object {
            fun encrypt(password: String, key: String?): String {
                val cipher = Cipher.getInstance("AES")
                cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key?.toByteArray(), "AES"))
                val encrypted = cipher.doFinal(password.toByteArray())
                return Base64.getEncoder().encodeToString(encrypted)
            }

            fun decrypt(password: String, key: String) : String {
                val cipher = Cipher.getInstance("AES")
                cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key.toByteArray(), "AES"))
                val decoded = Base64.getDecoder().decode(password)
                return String(cipher.doFinal(decoded))
            }
        }
    }
}
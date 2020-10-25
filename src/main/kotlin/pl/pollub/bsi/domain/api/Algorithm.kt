package pl.pollub.bsi.domain.api

import io.vavr.collection.Stream
import io.vavr.kotlin.toVavrStream

enum class Algorithm(val instance: String) {
    SHA_512("SHA-512"), HMAC("HMAC");

    companion object {
        fun resolve(name: String): Algorithm {
            return values()
                    .toVavrStream()
                    .find { name == it.instance }
                    .orNull
        }

        fun valueOf(name: String): Algorithm {
            return values()
                    .toVavrStream()
                    .find { it.instance == name }
                    .getOrElseThrow { RuntimeException() }
        }
    }
}

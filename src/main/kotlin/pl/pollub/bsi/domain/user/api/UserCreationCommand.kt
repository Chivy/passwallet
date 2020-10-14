package pl.pollub.bsi.domain.user.api

import pl.pollub.bsi.domain.api.Algorithm

data class UserCreationCommand(
        private val login: String,
        private val password: String,
        private val algorithm: Algorithm
) {
}

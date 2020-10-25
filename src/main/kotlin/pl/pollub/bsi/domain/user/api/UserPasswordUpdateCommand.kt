package pl.pollub.bsi.domain.user.api

import pl.pollub.bsi.application.user.api.UpdatePasswordApplicationRequest

data class UserPasswordUpdateCommand(
        val password: String,
        val algorithm: String
) {

    companion object {
        internal fun of(updatePasswordApplicationRequest: UpdatePasswordApplicationRequest): UserPasswordUpdateCommand {
            return UserPasswordUpdateCommand(
                    updatePasswordApplicationRequest.newPassword,
                    updatePasswordApplicationRequest.algorithm.instance,
            )
        }
    }
}

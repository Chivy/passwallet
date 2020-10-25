package pl.pollub.bsi.application.password.api

data class CreatePasswordApplicationResponse(
        val id: String,
        val userId: String,
        val login: String,
        val password: String,
        val webAddress: String,
        val description: String
) {

}

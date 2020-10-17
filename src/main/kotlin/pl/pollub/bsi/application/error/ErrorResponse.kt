package pl.pollub.bsi.application.error

data class ErrorResponse(val message: String) {

    companion object {
        fun unexpected() : ErrorResponse {
            return ErrorResponse(
                    "Unexpected error occured."
            )
        }
    }
}

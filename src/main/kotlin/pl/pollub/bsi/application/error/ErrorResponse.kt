package pl.pollub.bsi.application.error

data class ErrorResponse(val message: String) {

    companion object {
        fun unexpected(): ErrorResponse {
            return ErrorResponse(
                    "Unexpected error occured."
            )
        }

        fun <T> notFoundById(objectName: String, id: T): ErrorResponse {
            return ErrorResponse(
                    "${objectName.capitalize()} not found by ID: ${id.toString()}"
            )
        }
    }
}

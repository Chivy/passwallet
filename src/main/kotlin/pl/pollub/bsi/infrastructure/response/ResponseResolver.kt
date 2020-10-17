package pl.pollub.bsi.infrastructure.response

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.vavr.control.Either
import pl.pollub.bsi.application.error.ErrorResponse
import javax.inject.Singleton

@Singleton
class ResponseResolver {
    fun resolve(response: Either<ErrorResponse, out Any>, status: HttpStatus): HttpResponse<Any> {
        return response
                .map { HttpResponse.ok(it).status(status) }
                .getOrElseGet { HttpResponse.badRequest(it) }
    }
}

package pl.pollub.bsi.infrastructure.response

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.vavr.control.Either
import io.vavr.control.Option
import pl.pollub.bsi.application.error.ErrorResponse
import pl.pollub.bsi.domain.user.api.UserResponse
import javax.inject.Singleton

@Singleton
class ResponseResolver {
    fun resolve(response: Either<ErrorResponse, out Any>, status: HttpStatus): HttpResponse<Any> {
        return response
                .map { HttpResponse.ok(it).status(status) }
                .getOrElseGet { HttpResponse.badRequest(it) }
    }

    fun resolve(response: Option<out Any>): HttpResponse<Any> {
        return response
                .map { HttpResponse.ok(it).status(HttpStatus.OK) }
                .getOrElse { HttpResponse.notFound() }
    }
}

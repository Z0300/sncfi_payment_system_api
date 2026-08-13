package com.sncfi.payment_system.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String?
)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException) =
        respond(HttpStatus.NOT_FOUND, ex.message)

    @ExceptionHandler(InsufficientBalanceException::class, InvalidPaymentException::class)
    fun handleBadRequest(ex: RuntimeException) =
        respond(HttpStatus.BAD_REQUEST, ex.message)

    @ExceptionHandler(AccessDeniedForResourceException::class, AccessDeniedException::class)
    fun handleForbidden(ex: Exception) =
        respond(HttpStatus.FORBIDDEN, ex.message ?: "Access denied")

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException) =
        respond(HttpStatus.UNAUTHORIZED, "Invalid username or password")

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return respond(HttpStatus.BAD_REQUEST, message)
    }

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun handleInvalidRefreshToken(ex: InvalidRefreshTokenException) =
        respond(HttpStatus.UNAUTHORIZED, ex.message)

    private fun respond(status: HttpStatus, message: String?) =
        ResponseEntity.status(status).body(
            ErrorResponse(status = status.value(), error = status.reasonPhrase, message = message)
        )
}
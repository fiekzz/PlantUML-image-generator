package com.fiekzz.com.puml.utils.exception

import com.fiekzz.com.puml.model.apiresponse.ErrorResponse
import com.fiekzz.com.puml.model.appmessages.AppMessages
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.MaxUploadSizeExceededException
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<ErrorResponse<Map<String, String>>> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }

        return ResponseEntity.badRequest().body(
            ErrorResponse(
                message = "Validation failed",
                data = errors,
            )
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handlerConstraintViolation(
        ex: ConstraintViolationException,
    ): ResponseEntity<ErrorResponse<Map<String, String>>> {
        val errors = ex.constraintViolations.map { it.message }

        return ResponseEntity.badRequest().body(
            ErrorResponse(
                message = "Validation failed",
            )
        )
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxSizeException(
        ex: MaxUploadSizeExceededException
    ): ResponseEntity<ErrorResponse<String>> {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
            ErrorResponse(
                message = AppMessages.File.exceededFileSize
            )
        )
    }

}
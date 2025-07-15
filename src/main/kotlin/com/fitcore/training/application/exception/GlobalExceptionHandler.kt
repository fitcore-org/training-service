package com.fitcore.training.application.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.beans.TypeMismatchException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    // 404 - Not Found: Exercise not found
    @ExceptionHandler(ExerciseNotFoundException::class)
    fun handleExerciseNotFound(ex: ExerciseNotFoundException, request: WebRequest): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.NOT_FOUND, ex.message ?: "Exercise not found.")

    // 404 - Not Found: Workout template not found
    @ExceptionHandler(WorkoutTemplateNotFoundException::class)
    fun handleWorkoutTemplateNotFound(ex: WorkoutTemplateNotFoundException, request: WebRequest): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.NOT_FOUND, ex.message ?: "Workout template not found.")

    // 400 - Bad Request: Invalid exercise reference
    @ExceptionHandler(InvalidExerciseReferenceException::class)
    fun handleInvalidExerciseReference(ex: InvalidExerciseReferenceException, request: WebRequest): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid exercise reference.")

    // 422 - Unprocessable Entity: Workout validation error
    @ExceptionHandler(WorkoutValidationException::class)
    fun handleWorkoutValidation(ex: WorkoutValidationException, request: WebRequest): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.message ?: "Workout validation failed.")

    // 400 - Bad Request: IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: Exception, request: WebRequest): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid request.")

    // 404 - Not Found (NoSuchElementException)
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException, request: WebRequest): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.NOT_FOUND, ex.message ?: "Resource not found.")

    // 422 - Unprocessable Entity: Bean validation error
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val errors = ex.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, errors)
    }

    // 400 - Bad Request: missing required parameter
    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.BAD_REQUEST, ex.message ?: "Missing request parameter.")

    // 400 - Bad Request: missing multipart part
    override fun handleMissingServletRequestPart(
        ex: MissingServletRequestPartException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.BAD_REQUEST, ex.message ?: "Missing request part.")

    // 400 - Bad Request: servlet binding error
    override fun handleServletRequestBindingException(
        ex: ServletRequestBindingException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.BAD_REQUEST, ex.message ?: "Servlet request binding failed.")

    // 400 - Bad Request: type mismatch
    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.BAD_REQUEST, ex.message ?: "Type mismatch.")

    // 405 - Method Not Allowed
    override fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed.")

    // 415 - Unsupported Media Type
    override fun handleHttpMediaTypeNotSupported(
        ex: HttpMediaTypeNotSupportedException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type.")

    // 406 - Not Acceptable
    override fun handleHttpMediaTypeNotAcceptable(
        ex: HttpMediaTypeNotAcceptableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.NOT_ACCEPTABLE, "Not acceptable.")

    // 500 - Internal Server Error (catch-all)
    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception, request: WebRequest): ResponseEntity<Any> =
        buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.message ?: "Unexpected error occurred.")

    private fun buildErrorResponse(status: HttpStatus, message: String): ResponseEntity<Any> {
        val isDev = System.getenv("SPRING_PROFILES_ACTIVE") == "dev"
        val errorBody = mutableMapOf<String, Any>(
            "error" to status.reasonPhrase,
            "status" to status.value(),
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )

        if (isDev) {
            errorBody["debug"] = Thread.currentThread().stackTrace
                .drop(2)
                .take(5)
                .map { "${it.className}.${it.methodName}:${it.lineNumber}" }
        }
        
        return ResponseEntity.status(status).body(errorBody)
    }
}

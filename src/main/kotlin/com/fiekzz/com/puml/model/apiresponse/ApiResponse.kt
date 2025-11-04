package com.fiekzz.com.puml.model.apiresponse

abstract class ApiResponse<T> {
    abstract val success: Boolean
    abstract  val message: String
    abstract  val data: T?
}

data class SuccessResponse<T>(
    override val success: Boolean = true,
    override val message: String,
    override val data: T?
) : ApiResponse<T>()

data class ErrorResponse<T>(
    override val success: Boolean = false,
    override val message: String,
    override val data: T? = null
) : ApiResponse<T>()
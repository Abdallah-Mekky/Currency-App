package com.example.currency.core.exceptions

sealed class Result<out T> {
    data class Success<out T>(val data: T): Result<T>()
    data class NetworkException(val message: String) : Result<Nothing>()
    data class ApiException(val message: String) : Result<Nothing>()
    data class UnexpectedException(val message: String) : Result<Nothing>()
}
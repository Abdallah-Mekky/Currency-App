package com.example.currency.core.exceptions

/**
 * A sealed class that represents the result of an operation.
 *
 * @param T The type of data returned in case of success.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class NetworkException(val message: String) : Result<Nothing>()
    data class ApiException(val message: String) : Result<Nothing>()
    data class UnexpectedException(val message: String) : Result<Nothing>()
}
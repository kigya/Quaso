package com.kigya.foundation.model

typealias Mapper<Input, Output> = (Input) -> Output

/**
 * Base class which represents result of some async operation
 */
sealed class Result<T> {

    /**
     * Convert this result of type T into another result of type R
     */
    fun <R> map(mapper: Mapper<T, R>? = null): Result<R> = when (this) {
        is PendingResult -> PendingResult()
        is ErrorResult -> ErrorResult(exception = this.exception)
        is SuccessResult -> {
            requireNotNull(mapper) { "Mapper should be not null for success result" }
            SuccessResult(mapper(this.data))
        }
    }
}

/**
 * Operation has been finished
 */
sealed class FinalResult<T> : Result<T>()

/**
 * Operation is in progress
 */
class PendingResult<T> : Result<T>()

/**
 * Operation has finished successfully
 */
class SuccessResult<T>(
    val data: T
) : FinalResult<T>()

/**
 * Operation has finished with error
 */
class ErrorResult<T>(
    val exception: Exception
) : FinalResult<T>()

fun <T> Result<T>?.takeSuccess(): T? = (this as? SuccessResult)?.data

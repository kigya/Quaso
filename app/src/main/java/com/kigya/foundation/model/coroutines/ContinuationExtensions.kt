package com.kigya.foundation.model.coroutines

import com.kigya.foundation.model.ErrorResult
import com.kigya.foundation.model.FinalResult
import com.kigya.foundation.model.SuccessResult
import kotlinx.coroutines.CancellableContinuation
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Convert coroutine [CancellableContinuation] instance into simpler [Emitter] instance.
 * Every Continuation is associated with a suspension point.
 * A continuation is the implicit parameter that the Kotlin compiler passes to any suspend function
 * when compiling it. It is represented by a basic contract:
 * ```
 * interface Continuation<in T> {
 *  abstract val context: CoroutineContext
 *  abstract fun resumeWith(result: Result<T>)
 * }
 * ```
 * Under the hood, Kotlin will generate a ContinuationImpl for this contract for each suspended function.
 * So, the continuation decides how the program continues after some work, and that makes it another form of control flow.
 * It will be used to coordinate the work between all our suspend functions.
 */
fun <T> CancellableContinuation<T>.toEmitter(): Emitter<T> = object : Emitter<T> {
    var done = AtomicBoolean(false)
    override fun emit(finalResult: FinalResult<T>) {
        if (done.compareAndSet(false, true)) {
            when (finalResult) {
                is SuccessResult -> resume(value = finalResult.data)
                is ErrorResult -> resumeWithException(exception = finalResult.exception)
            }
        }
    }
    override fun setCancelListener(cancelListener: CancelListener) =
        invokeOnCancellation { cancelListener() }
}

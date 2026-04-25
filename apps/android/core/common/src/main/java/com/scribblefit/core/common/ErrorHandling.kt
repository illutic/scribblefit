package com.scribblefit.core.common

import kotlinx.coroutines.CancellationException

inline fun <T, R> T.runCatchingWithCancellation(
    tryBlock: T.() -> R,
) = try {
    Result.success(tryBlock())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Result.failure(e)
}

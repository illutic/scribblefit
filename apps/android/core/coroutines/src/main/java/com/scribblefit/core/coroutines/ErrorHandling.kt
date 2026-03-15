package com.scribblefit.core.coroutines

import kotlinx.coroutines.CancellationException

inline fun tryWithCancellationHandling(
    tryBlock: () -> Unit,
    catchBlock: (Throwable) -> Unit
) = try {
    tryBlock()
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    catchBlock(e)
}

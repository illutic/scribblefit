package com.scribblefit.core.model

import java.time.LocalDateTime
import java.time.ZoneId

@JvmInline
value class CurrentDate(
    val date: LocalDateTime
) {
    val millis: Long
        get() = date
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
}

package com.scribblefit.core.model

import java.time.LocalDate
import java.time.ZoneId

@JvmInline
value class CurrentDate(
    val date: LocalDate
) {
    val startOfDayInMillis: Long
        get() = date
            .atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
}

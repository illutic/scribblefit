package com.scribblefit.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class TrendDirection {
    IMPROVING, STABLE, DECLINING;
}

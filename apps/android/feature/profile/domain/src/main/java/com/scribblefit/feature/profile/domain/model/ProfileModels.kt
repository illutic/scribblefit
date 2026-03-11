package com.scribblefit.feature.profile.domain.model

data class UserStats(
    val totalWorkouts: Int,
    val lifetimeVolume: Double,
    val prCount: Int,
    val joinDate: String?
)

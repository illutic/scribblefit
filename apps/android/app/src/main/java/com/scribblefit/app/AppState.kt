package com.scribblefit.app

import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.navigation.NavState

data class AppState(
    val navState: NavState = NavState(),
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val isDynamicTheme: Boolean = false,
)

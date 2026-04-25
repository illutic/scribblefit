package com.scribblefit.core.navigation

import kotlinx.coroutines.flow.StateFlow

interface Navigator {
    val navState: StateFlow<NavState>

    fun navigateTo(screen: Screen)

    fun goBack()

    fun switchToTab(screen: Screen)
}

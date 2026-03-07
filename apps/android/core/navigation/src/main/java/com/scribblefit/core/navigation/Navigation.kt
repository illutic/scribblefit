package com.scribblefit.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {
    @Serializable data object Canvas : Screen
    @Serializable data object Analytics : Screen
    @Serializable data object Exercises : Screen
    @Serializable data object Profile : Screen
}

interface Navigator {
    val backStack: NavBackStack<Screen>
    fun navigateTo(screen: Screen)
    fun goBack()
    fun switchToTab(screen: Screen)
}

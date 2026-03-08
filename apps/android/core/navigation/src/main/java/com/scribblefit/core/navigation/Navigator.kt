package com.scribblefit.core.navigation

interface Navigator {
    fun navigateTo(screen: Screen)
    fun goBack()
    fun switchToTab(screen: Screen)
}

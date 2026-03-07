package com.scribblefit.app.navigation

import androidx.navigation3.runtime.NavBackStack
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigatorImpl @Inject constructor() : Navigator {
    override val backStack: NavBackStack<Screen> = NavBackStack(Screen.Canvas)

    override fun navigateTo(screen: Screen) {
        backStack.add(screen)
    }

    override fun goBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    override fun switchToTab(screen: Screen) {
        backStack.clear()
        backStack.add(screen)
    }
}

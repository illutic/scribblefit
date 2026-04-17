package com.scribblefit.app.navigation

import com.scribblefit.core.navigation.NavState
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NavigatorImpl
@Inject
constructor() : Navigator {
    private val _navState = MutableStateFlow(NavState())
    override val navState: StateFlow<NavState> = _navState.asStateFlow()

    override fun navigateTo(screen: Screen) {
        _navState.update {
            if (screen.isTop) {
                it.copy(
                    bottomBarState = it.bottomBarState.copy(selectedTab = screen),
                    backStack = listOf(screen),
                )
            } else {
                it.copy(
                    backStack = it.backStack + screen,
                )
            }
        }
    }

    override fun goBack() {
        val currentStack = _navState.value.backStack
        if (currentStack.size > 1) {
            _navState.update {
                it.copy(
                    backStack = currentStack.dropLast(1),
                    bottomBarState =
                        it.bottomBarState.copy(
                            selectedTab = currentStack.lastOrNull { screen -> screen.isTop }
                                ?: it.bottomBarState.selectedTab,
                        ),
                )
            }
        }
    }

    override fun switchToTab(screen: Screen) {
        if (screen.isTop) {
            _navState.update {
                it.copy(
                    bottomBarState = it.bottomBarState.copy(selectedTab = screen),
                    backStack = listOf(screen),
                )
            }
        }
    }
}

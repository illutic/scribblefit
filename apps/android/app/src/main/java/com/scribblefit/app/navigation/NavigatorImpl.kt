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
        _navState.update { state ->
            if (screen.isTop) {
                val currentStack = state.backStack

                // Filter out non-top screens to clear them, keeping only main tabs to preserve state
                val topScreens = currentStack.filter { it.isTop }

                // Use the new screen instance to ensure new arguments are not dropped
                val targetScreen = screen
                val remainingTopScreens = topScreens.filter { it.javaClass != screen.javaClass }

                val canvasScreen = remainingTopScreens.firstOrNull { it is Screen.Canvas }

                val newStack = mutableListOf<Screen>()
                if (canvasScreen != null) {
                    newStack.add(canvasScreen)
                }
                newStack.addAll(remainingTopScreens.filter { it !is Screen.Canvas })

                // If the target is Canvas, we don't add it twice
                if (targetScreen !is Screen.Canvas || canvasScreen == null) {
                    newStack.add(targetScreen)
                } else {
                    // If target is Canvas and we already added canvasScreen, replace it at the top
                    newStack.remove(canvasScreen)
                    newStack.add(targetScreen)
                }

                state.copy(
                    bottomBarState = state.bottomBarState.copy(selectedTab = screen),
                    backStack = newStack.toList(),
                )
            } else {
                // Prevent duplicate consecutive screens
                if (state.backStack.lastOrNull() == screen) {
                    state
                } else {
                    state.copy(
                        backStack = state.backStack + screen,
                    )
                }
            }
        }
    }

    override fun goBack() {
        val currentStack = _navState.value.backStack
        if (currentStack.size > 1) {
            _navState.update { state ->
                val newStack = currentStack.dropLast(1)
                state.copy(
                    backStack = newStack,
                    bottomBarState =
                        state.bottomBarState.copy(
                            selectedTab = newStack.lastOrNull { screen -> screen.isTop }
                                ?: state.bottomBarState.selectedTab,
                        ),
                )
            }
        }
    }

    override fun switchToTab(screen: Screen) {
        navigateTo(screen)
    }
}

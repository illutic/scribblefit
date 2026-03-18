package com.scribblefit.app.navigation

import com.scribblefit.core.navigation.NavState
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NavigatorImpl
    @Inject
    constructor() : Navigator {
        private val _navState = MutableStateFlow(NavState())
        override val navState: StateFlow<NavState> = _navState.asStateFlow()

        override fun navigateTo(screen: Screen) {
            _navState.value =
                _navState.value.copy(
                    backStack = _navState.value.backStack + screen,
                )
        }

        override fun goBack() {
            val currentStack = _navState.value.backStack
            if (currentStack.size > 1) {
                _navState.value =
                    _navState.value.copy(
                        backStack = currentStack.dropLast(1),
                    )
            }
        }

        override fun switchToTab(screen: Screen) {
            _navState.value =
                _navState.value.copy(
                    backStack = listOf(screen),
                    bottomBarState =
                        _navState.value.bottomBarState.copy(
                            selectedTab = screen,
                        ),
                )
        }
    }

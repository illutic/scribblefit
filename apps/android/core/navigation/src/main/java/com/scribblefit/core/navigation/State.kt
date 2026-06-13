package com.scribblefit.core.navigation

data class NavState(
    val backStack: List<Screen> = listOf(Screen.Canvas()),
    val bottomBarState: BottomBarState = BottomBarState()
)

data class BottomBarState(
    val selectedTab: Screen = Screen.Canvas(),
    val isVisible: Boolean = true,
    val items: List<BottomBarItem> = listOf(
        BottomBarItem(Screen.Canvas()),
        BottomBarItem(Screen.Insights),
        BottomBarItem(Screen.Ledger)
    )
)

@JvmInline
value class BottomBarItem(val screen: Screen)

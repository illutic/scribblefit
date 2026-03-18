package com.scribblefit.app

sealed interface AppIntent {
    data object NavigateBack : AppIntent
}

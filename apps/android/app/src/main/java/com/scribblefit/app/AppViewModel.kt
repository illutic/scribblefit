package com.scribblefit.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel
    @Inject
    constructor(
        private val navigator: Navigator,
        configRepository: ConfigRepository,
    ) : ViewModel() {
        val appState =
            combine(
                navigator.navState,
                configRepository.config,
            ) { navState, config ->
                AppState(
                    navState = navState,
                    themePreference = config.themePreference,
                )
            }.stateIn(viewModelScope, SharingStarted.Eagerly, AppState())

        fun onIntent(appIntent: AppIntent) =
            when (appIntent) {
                AppIntent.NavigateBack -> navigator.goBack()
            }
    }

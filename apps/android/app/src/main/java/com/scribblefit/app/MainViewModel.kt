package com.scribblefit.app

import android.content.Context
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val configRepository: ConfigRepository,
    private val navigator: Navigator
) : ViewModel() {
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized
    val backStack = navigator.navState
        .map { it.backStack }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf(Screen.Canvas))

    init {
        initializeApplication()
    }

    fun navigateTo(screen: Screen) = navigator.navigateTo(screen)

    fun navigateTab(screen: Screen) = navigator.switchToTab(screen)

    fun goBack() = navigator.goBack()

    private fun initializeApplication() {
        viewModelScope.launch {
            // 1. Get/Generate Device ID
            val deviceId =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                    ?: "unknown_device"

            // 2. Sync Metadata (prompt config)
            // configRepository.syncMetadata()

            _isInitialized.value = true
        }
    }
}

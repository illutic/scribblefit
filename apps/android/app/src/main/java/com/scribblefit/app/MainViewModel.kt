package com.scribblefit.app

import android.provider.Settings
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.ai.domain.repository.AuthRepository
import com.scribblefit.feature.ai.domain.repository.ConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val configRepository: ConfigRepository
) : ViewModel() {

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    init {
        initializeApplication()
    }

    private fun initializeApplication() {
        viewModelScope.launch {
            // 1. Get/Generate Device ID
            val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
            
            // 2. Perform Login/Auth if needed
            if (!authRepository.isLogged()) {
                authRepository.login(deviceId)
            }
            
            // 3. Sync Metadata & Exercises
            configRepository.syncMetadata()
            configRepository.syncExercises()
            
            _isInitialized.value = true
        }
    }
}

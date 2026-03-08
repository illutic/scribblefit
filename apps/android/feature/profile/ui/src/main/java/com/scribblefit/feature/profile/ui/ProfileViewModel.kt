package com.scribblefit.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.profile.domain.model.UserStats
import com.scribblefit.feature.profile.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val FLOW_TIMEOUT_MS = 5_000L

data class ProfileUiState(
    val userName: String = "George",
    val stats: UserStats? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val navigator: com.scribblefit.core.navigation.Navigator
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = userRepository.getUserStats()
        .map { stats ->
            ProfileUiState(stats = stats, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
            initialValue = ProfileUiState()
        )

    fun onSettingsClick() {
        navigator.navigateTo(com.scribblefit.core.navigation.Screen.Settings)
    }
}

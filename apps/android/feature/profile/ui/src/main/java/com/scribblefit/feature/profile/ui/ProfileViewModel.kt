package com.scribblefit.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.profile.domain.model.UserStats
import com.scribblefit.feature.profile.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

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
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileUiState()
        )

    fun onSettingsClick() {
        navigator.navigateTo(com.scribblefit.core.navigation.Screen.Settings)
    }
}

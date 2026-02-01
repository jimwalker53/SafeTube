package com.safetube.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safetube.data.local.preferences.AppPreferences
import com.safetube.data.local.preferences.SecurePreferences
import com.safetube.data.repository.AuthRepository
import com.safetube.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val appPreferences: AppPreferences,
    private val securePreferences: SecurePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val account = authRepository.getCurrentAccount()
            _uiState.update { state ->
                state.copy(
                    userEmail = account?.email,
                    userName = account?.displayName,
                    isSignedIn = authRepository.isSignedIn(),
                    autoplayEnabled = appPreferences.isAutoplayEnabled(),
                    allowedOnlyMode = appPreferences.isAllowedOnlyMode()
                )
            }
        }
    }

    fun setAutoplayEnabled(enabled: Boolean) {
        viewModelScope.launch {
            appPreferences.setAutoplayEnabled(enabled)
            _uiState.update { it.copy(autoplayEnabled = enabled) }
        }
    }

    fun setAllowedOnlyMode(enabled: Boolean) {
        viewModelScope.launch {
            appPreferences.setAllowedOnlyMode(enabled)
            _uiState.update { it.copy(allowedOnlyMode = enabled) }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (authRepository.signOut()) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isSignedIn = false,
                            userEmail = null,
                            userName = null,
                            isLoading = false,
                            signOutSuccess = true
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                }

                is Result.Loading -> {}
            }
        }
    }

    fun clearSignOutSuccess() {
        _uiState.update { it.copy(signOutSuccess = false) }
    }
}

data class SettingsUiState(
    val userEmail: String? = null,
    val userName: String? = null,
    val isSignedIn: Boolean = false,
    val autoplayEnabled: Boolean = true,
    val allowedOnlyMode: Boolean = false,
    val isLoading: Boolean = false,
    val signOutSuccess: Boolean = false
)

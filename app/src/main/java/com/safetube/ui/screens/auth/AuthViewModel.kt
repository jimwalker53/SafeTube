package com.safetube.ui.screens.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkSignInStatus()
    }

    private fun checkSignInStatus() {
        _uiState.update { state ->
            state.copy(isSignedIn = authRepository.isSignedIn())
        }
    }

    fun getSignInIntent(): Intent {
        return authRepository.getSignInIntent()
    }

    fun handleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = authRepository.handleSignInResult(data)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isSignedIn = true,
                            isLoading = false,
                            userEmail = result.data.email,
                            userName = result.data.displayName,
                            signInSuccess = true
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }

                is Result.Loading -> {}
            }
        }
    }

    fun clearSignInSuccess() {
        _uiState.update { it.copy(signInSuccess = false) }
    }
}

data class AuthUiState(
    val isSignedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val userEmail: String? = null,
    val userName: String? = null,
    val signInSuccess: Boolean = false
)

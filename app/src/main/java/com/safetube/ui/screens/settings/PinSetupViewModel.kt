package com.safetube.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safetube.data.local.preferences.SecurePreferences
import com.safetube.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinSetupViewModel @Inject constructor(
    private val securePreferences: SecurePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinSetupUiState())
    val uiState: StateFlow<PinSetupUiState> = _uiState.asStateFlow()

    fun updatePin(pin: String) {
        if (pin.length > _uiState.value.pinLength) return

        _uiState.update { state ->
            state.copy(currentPin = pin, error = null)
        }

        if (pin.length == _uiState.value.pinLength) {
            if (_uiState.value.isConfirming) {
                confirmPin(pin)
            } else {
                proceedToConfirmation(pin)
            }
        }
    }

    private fun proceedToConfirmation(pin: String) {
        _uiState.update { state ->
            state.copy(
                firstPin = pin,
                currentPin = "",
                isConfirming = true
            )
        }
    }

    private fun confirmPin(confirmPin: String) {
        val firstPin = _uiState.value.firstPin

        if (firstPin == confirmPin) {
            // PINs match, save it
            viewModelScope.launch {
                securePreferences.savePin(confirmPin)
                _uiState.update { state ->
                    state.copy(pinSetSuccess = true)
                }
            }
        } else {
            // PINs don't match, reset
            _uiState.update { state ->
                state.copy(
                    firstPin = "",
                    currentPin = "",
                    isConfirming = false,
                    error = "PINs do not match. Please try again."
                )
            }
        }
    }
}

data class PinSetupUiState(
    val pinLength: Int = Constants.PIN_MIN_LENGTH,
    val firstPin: String = "",
    val currentPin: String = "",
    val isConfirming: Boolean = false,
    val error: String? = null,
    val pinSetSuccess: Boolean = false
)

package com.safetube.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safetube.data.local.preferences.SecurePreferences
import com.safetube.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinEntryViewModel @Inject constructor(
    private val securePreferences: SecurePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinEntryUiState())
    val uiState: StateFlow<PinEntryUiState> = _uiState.asStateFlow()

    private var lockoutJob: Job? = null

    init {
        loadPinLength()
    }

    private fun loadPinLength() {
        viewModelScope.launch {
            val savedPin = securePreferences.getPin()
            val pinLength = savedPin?.length ?: Constants.PIN_MIN_LENGTH
            _uiState.update { it.copy(pinLength = pinLength) }
        }
    }

    fun updatePin(pin: String) {
        if (_uiState.value.isLockedOut) return
        if (pin.length > _uiState.value.pinLength) return

        _uiState.update { state ->
            state.copy(currentPin = pin, error = null)
        }

        if (pin.length == _uiState.value.pinLength) {
            verifyPin(pin)
        }
    }

    private fun verifyPin(enteredPin: String) {
        viewModelScope.launch {
            val savedPin = securePreferences.getPin()

            if (savedPin == enteredPin) {
                // PIN correct
                securePreferences.resetFailedAttempts()
                _uiState.update { state ->
                    state.copy(pinVerified = true)
                }
            } else {
                // PIN incorrect
                val attempts = securePreferences.incrementFailedAttempts()

                if (attempts >= Constants.PIN_MAX_ATTEMPTS) {
                    // Long lockout
                    startLockout(Constants.PIN_LOCKOUT_DURATION_LONG)
                } else if (attempts >= 3) {
                    // Short lockout after 3 attempts
                    startLockout(Constants.PIN_LOCKOUT_DURATION_SHORT)
                } else {
                    _uiState.update { state ->
                        state.copy(
                            currentPin = "",
                            error = "Incorrect PIN. ${Constants.PIN_MAX_ATTEMPTS - attempts} attempts remaining."
                        )
                    }
                }
            }
        }
    }

    private fun startLockout(durationMs: Long) {
        lockoutJob?.cancel()

        val durationSeconds = (durationMs / 1000).toInt()

        _uiState.update { state ->
            state.copy(
                currentPin = "",
                isLockedOut = true,
                lockoutRemainingSeconds = durationSeconds,
                error = null
            )
        }

        lockoutJob = viewModelScope.launch {
            for (remaining in durationSeconds downTo 1) {
                _uiState.update { it.copy(lockoutRemainingSeconds = remaining) }
                delay(1000)
            }

            _uiState.update { state ->
                state.copy(
                    isLockedOut = false,
                    lockoutRemainingSeconds = 0
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        lockoutJob?.cancel()
    }
}

data class PinEntryUiState(
    val pinLength: Int = Constants.PIN_MIN_LENGTH,
    val currentPin: String = "",
    val error: String? = null,
    val pinVerified: Boolean = false,
    val isLockedOut: Boolean = false,
    val lockoutRemainingSeconds: Int = 0
)

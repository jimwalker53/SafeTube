package com.safetube.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.safetube.R
import com.safetube.ui.components.PinInput

@Composable
fun PinSetupScreen(
    onPinSet: () -> Unit,
    viewModel: PinSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.pinSetSuccess) {
        if (uiState.pinSetSuccess) {
            onPinSet()
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            PinInput(
                title = if (uiState.isConfirming) {
                    stringResource(R.string.pin_confirm_title)
                } else {
                    stringResource(R.string.pin_setup_title)
                },
                subtitle = if (!uiState.isConfirming) {
                    stringResource(R.string.pin_setup_description)
                } else null,
                errorMessage = uiState.error,
                pinLength = uiState.pinLength,
                currentPin = uiState.currentPin,
                onPinChange = { viewModel.updatePin(it) }
            )
        }
    }
}

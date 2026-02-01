package com.safetube.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinEntryScreen(
    onPinVerified: () -> Unit,
    onCancel: () -> Unit,
    viewModel: PinEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.pinVerified) {
        if (uiState.pinVerified) {
            onPinVerified()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            PinInput(
                title = stringResource(R.string.pin_enter_title),
                errorMessage = when {
                    uiState.isLockedOut -> stringResource(
                        R.string.pin_lockout,
                        uiState.lockoutRemainingSeconds
                    )
                    uiState.error != null -> uiState.error
                    else -> null
                },
                pinLength = uiState.pinLength,
                currentPin = uiState.currentPin,
                onPinChange = { viewModel.updatePin(it) }
            )
        }
    }
}

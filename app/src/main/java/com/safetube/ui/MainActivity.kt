package com.safetube.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.safetube.data.local.preferences.SecurePreferences
import com.safetube.data.repository.AuthRepository
import com.safetube.ui.navigation.SafeTubeNavGraph
import com.safetube.ui.navigation.Screen
import com.safetube.ui.theme.SafeTubeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var securePreferences: SecurePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SafeTubeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var startDestination by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        // Determine start destination based on app state
                        startDestination = when {
                            // Check if PIN is set
                            !securePreferences.isPinSet() -> {
                                if (authRepository.isSignedIn()) {
                                    Screen.PinSetup.route
                                } else {
                                    Screen.SignIn.route
                                }
                            }
                            // Check if user is signed in
                            authRepository.isSignedIn() -> Screen.Home.route
                            // Not signed in, show sign in screen
                            else -> Screen.SignIn.route
                        }
                    }

                    // Keep splash screen visible until we determine the start destination
                    splashScreen.setKeepOnScreenCondition {
                        startDestination == null
                    }

                    startDestination?.let { destination ->
                        SafeTubeNavGraph(
                            navController = navController,
                            startDestination = destination
                        )
                    }
                }
            }
        }
    }
}

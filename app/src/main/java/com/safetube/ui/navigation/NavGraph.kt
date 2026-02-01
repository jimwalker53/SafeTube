package com.safetube.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.safetube.ui.screens.auth.SignInScreen
import com.safetube.ui.screens.home.HomeScreen
import com.safetube.ui.screens.player.PlayerScreen
import com.safetube.ui.screens.search.SearchScreen
import com.safetube.ui.screens.settings.BlockedChannelsScreen
import com.safetube.ui.screens.settings.BlockedKeywordsScreen
import com.safetube.ui.screens.settings.BlockedTermsScreen
import com.safetube.ui.screens.settings.PinEntryScreen
import com.safetube.ui.screens.settings.PinSetupScreen
import com.safetube.ui.screens.settings.SettingsScreen
import com.safetube.ui.screens.subscriptions.SubscriptionsScreen

/**
 * Navigation routes for the app.
 */
sealed class Screen(val route: String) {
    data object SignIn : Screen("sign_in")
    data object PinSetup : Screen("pin_setup")
    data object PinEntry : Screen("pin_entry")
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Subscriptions : Screen("subscriptions")
    data object Player : Screen("player/{videoId}") {
        fun createRoute(videoId: String) = "player/$videoId"
    }
    data object Settings : Screen("settings")
    data object BlockedTerms : Screen("settings/blocked_terms")
    data object BlockedKeywords : Screen("settings/blocked_keywords")
    data object BlockedChannels : Screen("settings/blocked_channels")
}

@Composable
fun SafeTubeNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(Screen.PinSetup.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onSkipToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PinSetup.route) {
            PinSetupScreen(
                onPinSet = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.PinSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PinEntry.route) {
            PinEntryScreen(
                onPinVerified = {
                    navController.navigate(Screen.Settings.route) {
                        popUpTo(Screen.PinEntry.route) { inclusive = true }
                    }
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onVideoClick = { videoId ->
                    navController.navigate(Screen.Player.createRoute(videoId))
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToSubscriptions = {
                    navController.navigate(Screen.Subscriptions.route)
                },
                onLongPressLogo = {
                    navController.navigate(Screen.PinEntry.route)
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onVideoClick = { videoId ->
                    navController.navigate(Screen.Player.createRoute(videoId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Subscriptions.route) {
            SubscriptionsScreen(
                onVideoClick = { videoId ->
                    navController.navigate(Screen.Player.createRoute(videoId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("videoId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
            PlayerScreen(
                videoId = videoId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRelatedVideoClick = { relatedVideoId ->
                    navController.navigate(Screen.Player.createRoute(relatedVideoId)) {
                        popUpTo(Screen.Player.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToBlockedTerms = {
                    navController.navigate(Screen.BlockedTerms.route)
                },
                onNavigateToBlockedKeywords = {
                    navController.navigate(Screen.BlockedKeywords.route)
                },
                onNavigateToBlockedChannels = {
                    navController.navigate(Screen.BlockedChannels.route)
                },
                onSignOut = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.BlockedTerms.route) {
            BlockedTermsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.BlockedKeywords.route) {
            BlockedKeywordsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.BlockedChannels.route) {
            BlockedChannelsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

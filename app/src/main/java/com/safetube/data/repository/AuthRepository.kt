package com.safetube.data.repository

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.safetube.BuildConfig
import com.safetube.data.local.preferences.SecurePreferences
import com.safetube.util.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val securePreferences: SecurePreferences
) {

    companion object {
        private const val YOUTUBE_READONLY_SCOPE = "https://www.googleapis.com/auth/youtube.readonly"
        private const val YOUTUBE_SCOPE = "https://www.googleapis.com/auth/youtube"
    }

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(
                Scope(YOUTUBE_READONLY_SCOPE),
                Scope(YOUTUBE_SCOPE)
            )
            .requestServerAuthCode(BuildConfig.GOOGLE_CLIENT_ID, true)
            .build()

        GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun isSignedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account != null && !account.isExpired
    }

    fun getCurrentAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    suspend fun getAccessToken(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val account = GoogleSignIn.getLastSignedInAccount(context)
                if (account != null) {
                    // Silently refresh the token
                    val result = googleSignInClient.silentSignIn().await()
                    result.serverAuthCode?.let { authCode ->
                        // In a production app, you would exchange this auth code for an access token
                        // using your backend server. For simplicity, we store the auth code.
                        securePreferences.saveAuthToken(authCode)
                        authCode
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                // Try to get cached token
                securePreferences.getAuthToken()
            }
        }
    }

    suspend fun handleSignInResult(data: Intent?): Result<GoogleSignInAccount> {
        return withContext(Dispatchers.IO) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.await()

                // Save the auth code
                account.serverAuthCode?.let { authCode ->
                    securePreferences.saveAuthToken(authCode)
                }

                Result.Success(account)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Sign-in failed")
            }
        }
    }

    suspend fun signOut(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                googleSignInClient.signOut().await()
                securePreferences.clearAuthToken()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Sign-out failed")
            }
        }
    }

    suspend fun revokeAccess(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                googleSignInClient.revokeAccess().await()
                securePreferences.clearAuthToken()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Revoke access failed")
            }
        }
    }
}

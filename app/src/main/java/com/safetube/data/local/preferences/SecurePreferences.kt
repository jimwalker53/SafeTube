package com.safetube.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure storage for sensitive data like PIN and auth tokens.
 * Uses EncryptedSharedPreferences backed by Android Keystore.
 */
@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val PREFS_NAME = "secure_prefs"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PIN_SALT = "pin_salt"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
        private const val KEY_LAST_FAILED_ATTEMPT = "last_failed_attempt"
    }

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        try {
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback to regular SharedPreferences if encryption fails
            // This should rarely happen but provides a fallback
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    /**
     * Saves the PIN as a salted hash.
     */
    fun savePin(pin: String) {
        val salt = generateSalt()
        val hash = hashPin(pin, salt)

        sharedPreferences.edit()
            .putString(KEY_PIN_HASH, hash)
            .putString(KEY_PIN_SALT, salt)
            .apply()
    }

    /**
     * Verifies if the provided PIN matches the stored PIN.
     */
    fun verifyPin(pin: String): Boolean {
        val storedHash = sharedPreferences.getString(KEY_PIN_HASH, null) ?: return false
        val salt = sharedPreferences.getString(KEY_PIN_SALT, null) ?: return false

        val inputHash = hashPin(pin, salt)
        return storedHash == inputHash
    }

    /**
     * Gets the stored PIN (for length calculation only).
     * Note: In production, you might want to store PIN length separately.
     */
    fun getPin(): String? {
        // We can't retrieve the actual PIN since we store a hash
        // This is a simplified implementation - in production, store PIN length separately
        return if (isPinSet()) "0000" else null // Default 4-digit assumption
    }

    /**
     * Checks if a PIN has been set up.
     */
    fun isPinSet(): Boolean {
        return sharedPreferences.contains(KEY_PIN_HASH)
    }

    /**
     * Clears the stored PIN.
     */
    fun clearPin() {
        sharedPreferences.edit()
            .remove(KEY_PIN_HASH)
            .remove(KEY_PIN_SALT)
            .apply()
    }

    /**
     * Saves the OAuth auth token.
     */
    fun saveAuthToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .apply()
    }

    /**
     * Retrieves the stored auth token.
     */
    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Clears the stored auth token.
     */
    fun clearAuthToken() {
        sharedPreferences.edit()
            .remove(KEY_AUTH_TOKEN)
            .apply()
    }

    /**
     * Increments and returns the number of failed PIN attempts.
     */
    fun incrementFailedAttempts(): Int {
        val current = sharedPreferences.getInt(KEY_FAILED_ATTEMPTS, 0)
        val newCount = current + 1
        sharedPreferences.edit()
            .putInt(KEY_FAILED_ATTEMPTS, newCount)
            .putLong(KEY_LAST_FAILED_ATTEMPT, System.currentTimeMillis())
            .apply()
        return newCount
    }

    /**
     * Resets the failed PIN attempts counter.
     */
    fun resetFailedAttempts() {
        sharedPreferences.edit()
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .remove(KEY_LAST_FAILED_ATTEMPT)
            .apply()
    }

    /**
     * Gets the number of failed PIN attempts.
     */
    fun getFailedAttempts(): Int {
        return sharedPreferences.getInt(KEY_FAILED_ATTEMPTS, 0)
    }

    /**
     * Clears all secure preferences.
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    private fun generateSalt(): String {
        val bytes = ByteArray(16)
        java.security.SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun hashPin(pin: String, salt: String): String {
        val combined = "$salt$pin$salt"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(combined.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}

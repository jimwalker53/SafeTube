package com.safetube.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Storage for general app preferences (non-sensitive data).
 */
@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_AUTOPLAY_ENABLED = "autoplay_enabled"
        private const val KEY_VIDEO_QUALITY = "video_quality"
        private const val KEY_ALLOWED_ONLY_MODE = "allowed_only_mode"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DARK_MODE = "dark_mode"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Checks if autoplay is enabled.
     */
    fun isAutoplayEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_AUTOPLAY_ENABLED, true)
    }

    /**
     * Sets whether autoplay is enabled.
     */
    fun setAutoplayEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_AUTOPLAY_ENABLED, enabled)
            .apply()
    }

    /**
     * Gets the preferred video quality.
     */
    fun getVideoQuality(): String {
        return sharedPreferences.getString(KEY_VIDEO_QUALITY, "auto") ?: "auto"
    }

    /**
     * Sets the preferred video quality.
     */
    fun setVideoQuality(quality: String) {
        sharedPreferences.edit()
            .putString(KEY_VIDEO_QUALITY, quality)
            .apply()
    }

    /**
     * Checks if allowed-only mode is enabled (only show subscribed channels).
     */
    fun isAllowedOnlyMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_ALLOWED_ONLY_MODE, false)
    }

    /**
     * Sets whether allowed-only mode is enabled.
     */
    fun setAllowedOnlyMode(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_ALLOWED_ONLY_MODE, enabled)
            .apply()
    }

    /**
     * Checks if this is the first launch of the app.
     */
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    /**
     * Marks that the app has been launched before.
     */
    fun setFirstLaunchComplete() {
        sharedPreferences.edit()
            .putBoolean(KEY_FIRST_LAUNCH, false)
            .apply()
    }

    /**
     * Gets the dark mode setting.
     * Returns: "system" (follow system), "light", or "dark"
     */
    fun getDarkModeSetting(): String {
        return sharedPreferences.getString(KEY_DARK_MODE, "system") ?: "system"
    }

    /**
     * Sets the dark mode setting.
     */
    fun setDarkModeSetting(setting: String) {
        sharedPreferences.edit()
            .putString(KEY_DARK_MODE, setting)
            .apply()
    }

    /**
     * Clears all app preferences.
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}

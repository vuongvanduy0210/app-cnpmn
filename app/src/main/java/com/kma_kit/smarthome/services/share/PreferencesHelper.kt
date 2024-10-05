@file:Suppress("DEPRECATION")

import android.preference.PreferenceManager
import com.kma_kit.smarthome.ui.SmartHomeApplication

class PreferencesHelper private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: PreferencesHelper? = null

        fun getInstance(): PreferencesHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferencesHelper().also { INSTANCE = it }
            }
        }

        private const val KEY_AuthToken = "auth_token"
        private const val KEY_enable_dark_mode = "enable_dark_mode"
        private const val KEY_enable_notification = "enable_notification"
    }

    private val preferences =
        PreferenceManager.getDefaultSharedPreferences(SmartHomeApplication.getAppContext())

    var authToken: String?
        get() = preferences.getString(KEY_AuthToken, null)
        set(value) = preferences.edit().putString(KEY_AuthToken, value).apply()

    var enableDarkMode: Boolean
        get() = preferences.getBoolean(KEY_enable_dark_mode, false)
        set(value) = preferences.edit().putBoolean(KEY_enable_dark_mode, value).apply()

    var enableNotification: Boolean
        get() = preferences.getBoolean(KEY_enable_notification, false)
        set(value) = preferences.edit().putBoolean(KEY_enable_notification, value).apply()

    fun clear() {
        preferences.edit().clear().apply()
    }
}

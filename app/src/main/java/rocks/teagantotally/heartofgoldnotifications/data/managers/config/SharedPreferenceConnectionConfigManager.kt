package rocks.teagantotally.heartofgoldnotifications.data.managers.config

import android.content.SharedPreferences
import com.google.gson.Gson
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration

class SharedPreferenceConnectionConfigManager(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : ConnectionConfigManager {

    companion object {
        private const val KEY_CONFIG = "ConnectionConfiguration"
    }

    override fun hasConnectionConfiguration(): Boolean =
        try {
            getConnectionConfiguration()
                .let { true }
        } catch (_: Throwable) {
            false
        }

    override fun getConnectionConfiguration(): ConnectionConfiguration? =
        sharedPreferences.getString(KEY_CONFIG, null)
            ?.let {
                try {
                    gson.fromJson(it, ConnectionConfiguration::class.java)
                } catch (_: Throwable) {
                    null
                }
            }

    override fun setConnectionConfiguration(connectionConfiguration: ConnectionConfiguration) {
        sharedPreferences
            .edit()
            .apply {
                putString(KEY_CONFIG, gson.toJson(connectionConfiguration))
            }
            .apply()
    }
}
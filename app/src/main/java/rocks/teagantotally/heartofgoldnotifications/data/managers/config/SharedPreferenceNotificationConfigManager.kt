package rocks.teagantotally.heartofgoldnotifications.data.managers.config

import android.content.SharedPreferences
import com.google.gson.Gson
import rocks.teagantotally.heartofgoldnotifications.common.extensions.asynchronousSave
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.NotificationConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.NotificationConfiguration

class SharedPreferenceNotificationConfigManager(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : NotificationConfigManager {
    companion object {
        private const val KEY_CONFIG = "NotificationConfiguration"
    }

    override fun hasConfiguration(): Boolean =
        getConfiguration() != null

    override fun getConfiguration(): NotificationConfiguration? =
        sharedPreferences.getString(KEY_CONFIG, null)
            ?.let {
                try {
                    gson.fromJson(it, NotificationConfiguration::class.java)
                } catch (_: Throwable) {
                    null
                }
            }

    override suspend fun saveConfiguration(configuration: NotificationConfiguration) {
        sharedPreferences
            .asynchronousSave {
                putString(KEY_CONFIG, gson.toJson(configuration))
            }
    }
}
package rocks.teagantotally.heartofgoldnotifications.domain.framework.managers

import android.content.SharedPreferences
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.NotificationConfiguration

interface NotificationConfigManager : ConfigManager<NotificationConfiguration> {
    fun addOnConfigurationChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)
    fun removeOnConfigurationChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener)
}
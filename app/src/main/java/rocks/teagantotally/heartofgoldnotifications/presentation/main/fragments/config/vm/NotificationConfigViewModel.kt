package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.vm

import android.support.v14.preference.SwitchPreference
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.PreferenceFragmentCompat
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.NotificationConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.NotificationConfiguration
import kotlin.reflect.KClass

class NotificationConfigViewModel(
    private val notificationConfigManager: NotificationConfigManager,
    fragment: PreferenceFragmentCompat
) : ConfigViewModel<NotificationConfiguration>(fragment) {
    override val preferenceTypeMap: Map<Int, KClass<*>> =
        mapOf(
            Pair(R.string.pref_notification_cancel_minutes, EditTextPreference::class),
            Pair(R.string.pref_notification_debug, SwitchPreference::class)
        )

    override fun populate() {
        retrieve()
            ?.run {
                setValue(R.string.pref_notification_cancel_minutes, notificationCancelMinutes)
                setValue(R.string.pref_notification_debug, debug)
            }
    }

    override suspend fun save() {
        retrieve()
            ?.let { notificationConfigManager.saveConfiguration(it) }
    }

    override fun isValid(): Boolean = retrieve() != null

    override fun retrieve(): NotificationConfiguration? =
        NotificationConfiguration(
            getValue(R.string.pref_notification_cancel_minutes, String::class)?.toIntOrNull()
                ?: NotificationConfiguration.DEFAULT_AUTO_CANCEL_MINUTES,
            getValue(R.string.pref_notification_debug, Boolean::class) ?: false
        )
}
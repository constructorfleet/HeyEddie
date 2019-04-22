package rocks.teagantotally.heartofgoldnotifications.domain.models.configs

import java.io.Serializable

data class NotificationConfiguration(
    val notificationCancelMinutes: Int = DEFAULT_AUTO_CANCEL_MINUTES,
    val debug: Boolean = false
) : Serializable, Configuration {
    companion object {
        const val DEFAULT_AUTO_CANCEL_MINUTES = 60
    }
}
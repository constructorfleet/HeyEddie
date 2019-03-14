package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ConnectionEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessageChannel

class UpdatePersistentNotificationUseCase(
    private val notifier: Notifier
) : UseCase<ConnectionEvent> {
    companion object {
        private const val PERSISTENT_NOTIFICATION_ID = -1009
        private val PERSISTENT_CHANNEL: NotificationMessageChannel =
            NotificationMessageChannel(
                "persistent",
                "Persistent",
                "Allows application to remain open in background"
            )

        fun getPersistentNotification(connected: Boolean) =
            NotificationMessage(
                PERSISTENT_CHANNEL,
                PERSISTENT_NOTIFICATION_ID,
                "Hey Eddie",
                when (connected) {
                    true -> "Connected to broker"
                    false -> "Disconnected"
                },
                onGoing = true,
                autoCancel = false
            )
    }

    override suspend fun invoke(parameter: ConnectionEvent) {
        notifier.notify(getPersistentNotification(parameter.isConnected))
    }
}
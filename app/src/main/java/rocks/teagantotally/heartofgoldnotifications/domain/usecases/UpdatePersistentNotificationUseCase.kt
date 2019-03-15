package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.clients.Client
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessageChannel

class UpdatePersistentNotificationUseCase(
    private val notifier: Notifier
) : UseCase<Client.ConnectionState> {
    companion object {
        private const val PERSISTENT_NOTIFICATION_ID = -1009
        private val PERSISTENT_CHANNEL: NotificationMessageChannel =
            NotificationMessageChannel(
                "persistent",
                "Persistent",
                "Allows application to remain open in background"
            )

        fun getPersistentNotification(state: Client.ConnectionState) =
            NotificationMessage(
                PERSISTENT_CHANNEL,
                PERSISTENT_NOTIFICATION_ID,
                "Hey Eddie",
                state.message,
                onGoing = true,
                autoCancel = false
            )
    }

    override suspend fun invoke(parameter: Client.ConnectionState) {
        notifier.notify(getPersistentNotification(parameter))
    }
}
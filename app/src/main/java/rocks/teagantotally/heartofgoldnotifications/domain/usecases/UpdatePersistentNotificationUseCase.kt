package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.EventProcessingUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseResult
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessageChannel
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientConnection
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientStatus

class UpdatePersistentNotificationUseCase(
    private val notifier: Notifier
) : EventProcessingUseCase<ClientStatus, Boolean>(ClientStatus::class) {
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

    override suspend fun handle(event: ClientStatus): UseCaseResult<Boolean> =
        notifier.notify(getPersistentNotification(event.isConnected))
            .let { UseCaseResult.Success(true) }
}
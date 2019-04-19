package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.models.ClientState
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationImportance
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessageChannel

class UpdatePersistentNotificationUseCase(
    private val notifier: Notifier
) : UseCaseWithParameter<ClientState> {
    companion object {
        private const val PERSISTENT_NOTIFICATION_ID = -1009
        val PERSISTENT_CHANNEL: NotificationMessageChannel =
            NotificationMessageChannel(
                "persistent",
                "Persistent",
                "Allows application to remain open in background",
                importance = NotificationImportance.MIN
            )

        fun getPersistentNotification(state: ClientState) =
            NotificationMessage(
                PERSISTENT_CHANNEL,
                PERSISTENT_NOTIFICATION_ID,
                "Hey Eddie",
                state.message,
                openApplication = true,
                onGoing = true,
                autoCancel = false
            )
    }

    override suspend fun invoke(parameter: ClientState) {
        notifier.notify(getPersistentNotification(parameter), false)
    }
}
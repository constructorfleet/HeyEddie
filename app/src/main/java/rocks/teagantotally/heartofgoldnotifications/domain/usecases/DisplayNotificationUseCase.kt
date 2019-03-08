package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import android.app.Notification
import rocks.teagantotally.heartofgoldnotifications.data.common.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.models.ReceivedMessage

class DisplayNotificationUseCase(
    private val convertToNotificationMessageUseCase: ConvertToNotificationMessageUseCase,
    private val notifier: Notifier,
    private val notificationBuilder: Notification.Builder
) : UseCase<ReceivedMessage, Unit?>() {
    override suspend fun invoke(params: ReceivedMessage) =
        convertToNotificationMessageUseCase(params)
            ?.let {
                notificationBuilder
                    .setChannelId(it.channel.id)
                    .setContentTitle(it.title)
                    .setContentText(it.body)
                    .build()
            }
            ?.let { notifier.notify(0, it) }
}
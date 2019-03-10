package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.processors.notifications.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessage
import kotlin.random.Random

class Notify(
    private val notifier: Notifier,
    private val convertToNotificationMessageUseCase: ConvertToNotificationMessageUseCase
) : UseCase<Message, NotificationMessage?>() {
    override suspend fun invoke(params: Message): NotificationMessage? =
        convertToNotificationMessageUseCase(params)
            ?.also { notifier.notify(it) }
}
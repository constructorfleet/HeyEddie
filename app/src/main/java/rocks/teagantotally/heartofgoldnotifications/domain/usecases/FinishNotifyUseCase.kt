package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.NotificationCommand

class FinishNotifyUseCase(
    private val notifier: Notifier
) : UseCase<NotificationCommand.Dismiss> {
    override suspend fun invoke(parameter: NotificationCommand.Dismiss) {
        notifier.dismiss(parameter.notificationId)
    }
}
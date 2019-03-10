package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseResult
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.NotificationActivated

class FinishNotifyUseCase(
    private val notifier: Notifier
) : UseCase<NotificationActivated, Boolean> {
    override suspend fun invoke(parameter: NotificationActivated): UseCaseResult<Boolean> =
            notifier.dismiss(parameter.notificationId)
                .let { UseCaseResult.Success(true) }
}
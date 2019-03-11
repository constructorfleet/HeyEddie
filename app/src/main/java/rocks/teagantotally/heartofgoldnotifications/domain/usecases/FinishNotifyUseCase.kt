package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.EventProcessingUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseResult
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.NotificationActivated

class FinishNotifyUseCase(
    private val notifier: Notifier
) : EventProcessingUseCase<NotificationActivated, Boolean>(NotificationActivated::class) {
    override suspend fun handle(event: NotificationActivated): UseCaseResult<Boolean> =
            notifier.dismiss(event.notificationId)
                .let { UseCaseResult.Success(true) }
}
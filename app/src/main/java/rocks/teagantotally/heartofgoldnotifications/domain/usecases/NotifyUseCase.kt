package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.EventProcessingUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseResult
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientMessageReceive

class NotifyUseCase(
    private val convert: ConvertToNotificationMessageUseCase,
    private val notifier: Notifier
) : EventProcessingUseCase<ClientMessageReceive.Successful, NotificationMessage>(ClientMessageReceive.Successful::class) {
    override suspend fun handle(event: ClientMessageReceive.Successful): UseCaseResult<NotificationMessage> =
        try {
            convert.invoke(event.message)
                .let {
                    when (it) {
                        is UseCaseResult.Success -> {
                            notifier.notify(it.value)
                            UseCaseResult.Success(it.value)
                        }
                        is UseCaseResult.Failure ->
                            UseCaseResult.Failure(it.throwable)
                        else -> it
                    }
                }
        } catch (t: Throwable) {
            UseCaseResult.Failure(t)
        }
}
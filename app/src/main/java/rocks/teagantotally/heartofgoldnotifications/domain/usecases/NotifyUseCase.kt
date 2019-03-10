package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseResult
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier

class NotifyUseCase(
    private val convert: ConvertToNotificationMessageUseCase,
    private val notifier: Notifier
) : UseCase<Message, NotificationMessage> {
    override suspend fun invoke(parameter: Message): UseCaseResult<NotificationMessage> =
        try {
            convert.invoke(parameter)
                .let {
                    when (it) {
                        is UseCaseResult.Success -> {
                            notifier.notify(it.value)
                            UseCaseResult.Success(it.value)
                        }
                        is UseCaseResult.Failure ->
                            UseCaseResult.Failure(it.throwable)
                    }
                }
        } catch (t: Throwable) {
            UseCaseResult.Failure(t)
        }
}
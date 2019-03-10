package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseResult
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientMessageReceive
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Event
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.NotificationActivated

class ProcessEventUseCase(
    private val notify: NotifyUseCase,
    private val finishNotify: FinishNotifyUseCase
) : UseCase<Event, Event> {
    override suspend fun invoke(parameter: Event): UseCaseResult<Event> =
        when (parameter) {
            is ClientMessageReceive.Successful ->
                notify(parameter.message)
                    .takeIf { it is UseCaseResult.Success }
                    ?.let { UseCaseResult.Success(parameter as Event) }
            is NotificationActivated ->
                finishNotify(parameter)
                    .let { UseCaseResult.Success(parameter as Event) }
            else -> null
        } ?: UseCaseResult.Failure(IllegalStateException("Unable to Process event"))
}
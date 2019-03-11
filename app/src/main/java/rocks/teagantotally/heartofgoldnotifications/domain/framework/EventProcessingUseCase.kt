package rocks.teagantotally.heartofgoldnotifications.domain.framework

import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Event
import kotlin.reflect.KClass

abstract class EventProcessingUseCase<EventType : Event, ResultType>(
    private val eventClass: KClass<EventType>
) : UseCase<Event, ResultType> {
    @Suppress("UNCHECKED_CAST")
    override suspend fun invoke(parameter: Event): UseCaseResult<ResultType> =
        when(eventClass.isInstance(parameter)) {
            true -> handle(parameter as EventType)
            false -> UseCaseResult.Unhandled()
        }

    protected abstract suspend fun handle(event: EventType): UseCaseResult<ResultType>
}
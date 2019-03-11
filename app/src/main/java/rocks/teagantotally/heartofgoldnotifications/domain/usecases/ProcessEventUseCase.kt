package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.EventProcessingUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseResult
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Event

class ProcessEventUseCase(
    private vararg val eventProcessors: EventProcessingUseCase<Event, Event>
) : UseCase<Event, Event> {
    override suspend fun invoke(parameter: Event): UseCaseResult<Event> =
        eventProcessors
            .map { process ->
                process(parameter)
            }
            .let { results ->
                results
                    .map { it as? UseCaseResult.Success }
                    .filterNotNull()
                    .let {
                        when (it.isEmpty()) {
                            true -> UseCaseResult.Success(parameter)
                            else -> UseCaseResult.Failure(IllegalStateException("Unable to process event"))
                        }
                    }
            }
}
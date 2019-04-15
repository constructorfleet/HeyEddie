package rocks.teagantotally.heartofgoldnotifications.domain.framework

import kotlinx.coroutines.channels.BroadcastChannel
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Event

interface UseCase {
    suspend operator fun invoke()
}

interface UseCasesWithReturn<ReturnType> {
    suspend operator fun invoke(): ReturnType
}

interface UseCaseWithParameter<in ParameterType> {
    suspend operator fun invoke(parameter: ParameterType)
}

interface SynchronousUseCase<ParameterType, ReturnType> {
    suspend operator fun invoke(parameter: ParameterType): ReturnType
}

interface UseCaseChannel<EventType : Event> : BroadcastChannel<EventType>
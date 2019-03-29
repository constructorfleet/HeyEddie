package rocks.teagantotally.heartofgoldnotifications.domain.framework

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
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

interface UseCaseChannel<EventType: Event> : BroadcastChannel<EventType>
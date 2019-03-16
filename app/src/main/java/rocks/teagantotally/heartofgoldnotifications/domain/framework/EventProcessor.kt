package rocks.teagantotally.heartofgoldnotifications.domain.framework

import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Event

interface EventProcessor<EventType: Event> {
    fun process(event: EventType)
}
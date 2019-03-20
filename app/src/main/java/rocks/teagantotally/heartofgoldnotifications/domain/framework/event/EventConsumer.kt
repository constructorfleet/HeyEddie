package rocks.teagantotally.heartofgoldnotifications.domain.framework.event

import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Event

interface EventConsumer<EventType : Event> {
    fun consume(event: EventType)
}
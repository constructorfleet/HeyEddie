package rocks.teagantotally.heartofgoldnotifications.domain.models.events

import rocks.teagantotally.heartofgoldnotifications.domain.models.Message

sealed class MessageEvent(val message: Message) : Event {
    class Received(message: Message) : MessageEvent(message)
    class Published(message: Message) : MessageEvent(message)
}
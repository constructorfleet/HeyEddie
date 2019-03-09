package rocks.teagantotally.heartofgoldnotifications.domain.models.events

import rocks.teagantotally.heartofgoldnotifications.domain.models.Message

sealed class CommandEvent {
    object Connect : CommandEvent()
    object Disconnect : CommandEvent()
    class Subscribe(val topic: String, val maxQoS: Int) : CommandEvent()
    class Unsubscribe(val topic: String): CommandEvent()
    class Publish(val message: Message): CommandEvent()
}
package rocks.teagantotally.heartofgoldnotifications.domain.models.commands

import rocks.teagantotally.heartofgoldnotifications.domain.models.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Event

sealed class ClientCommand: Event {
    data class SubscribeTo(val topic: String, val masQoS: Int) : ClientCommand()
    data class Unsubscribe(val topic: String): ClientCommand()
    data class Publish(val message: Message): ClientCommand()
}
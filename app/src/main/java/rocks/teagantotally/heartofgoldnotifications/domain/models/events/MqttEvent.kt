package rocks.teagantotally.heartofgoldnotifications.domain.models.events

import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

sealed class MqttEvent : Event {
    object Connected : MqttEvent()
    object Disconnected : MqttEvent()
    data class Subscribed(val topic: String) : MqttEvent()
    data class Unsubscribed(val topic: String) : MqttEvent()
    data class MessagePublished(val message: Message) : MqttEvent()
    data class MessageReceived(val message: Message) : MqttEvent()
    data class CommandFailed(val command: MqttCommand, val throwable: Throwable) : MqttEvent()
}
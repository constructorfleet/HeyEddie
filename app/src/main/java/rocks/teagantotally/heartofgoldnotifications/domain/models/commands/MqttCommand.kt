package rocks.teagantotally.heartofgoldnotifications.domain.models.commands

import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

sealed class MqttCommand : Command {
    object Connect : MqttCommand()

    object Disconnect : MqttCommand()

    class SubscribeTo(val topic: String, val maxQoS: Int) : MqttCommand()

    class Unsubscribe(val topic: String) : MqttCommand()

    class Publish(val message: Message) : MqttCommand()
}
package rocks.teagantotally.heartofgoldnotifications.domain.models.commands

import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

sealed class ClientCommand : Command {
    class SubscribeTo(val topic: String, val maxQoS: Int) : ClientCommand()

    class Unsubscribe(val topic: String) : ClientCommand()

    class Publish(val message: Message) : ClientCommand()
}
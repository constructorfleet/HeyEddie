package rocks.teagantotally.heartofgoldnotifications.domain.models.events

import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.ClientCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.EmptyMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

sealed class MessageEvent(val message: Message) : Event {
    open class Received(message: Message) : MessageEvent(message) {
        class Failed(override val source: Message = EmptyMessage, override val throwable: Throwable?) :
            MessageEvent.Received(source),
            Failure<Message>
    }

    open class Published(message: Message) : MessageEvent(message) {
        class Failed(override val source: ClientCommand.Publish, override val throwable: Throwable?) :
            MessageEvent.Published(source.message), Failure<ClientCommand.Publish>
    }
}
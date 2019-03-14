package rocks.teagantotally.heartofgoldnotifications.domain.models.events

import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.ClientCommand

sealed class SubscriptionEvent(val topic: String) : Event {
    open class Subscribed(topic: String) : SubscriptionEvent(topic) {
        class Failed(override val source: ClientCommand.SubscribeTo, override val throwable: Throwable?) :
            Subscribed(source.topic), Failure<ClientCommand.SubscribeTo>
    }

    open class Unsubscribed(topic: String) : SubscriptionEvent(topic) {
        class Failed(override val source: ClientCommand.Unsubscribe, override val throwable: Throwable?) :
            Unsubscribed(source.topic), Failure<ClientCommand.Unsubscribe>
    }
}
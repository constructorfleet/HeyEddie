package rocks.teagantotally.heartofgoldnotifications.domain.models.events

import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand

sealed class SubscriptionEvent(val topic: String) : Event {
    open class Subscribed(topic: String) : SubscriptionEvent(topic) {
        class Failed(override val source: MqttCommand.SubscribeTo, override val throwable: Throwable?) :
            Subscribed(source.topic), Failure<MqttCommand.SubscribeTo>
    }

    open class Unsubscribed(topic: String) : SubscriptionEvent(topic) {
        class Failed(override val source: MqttCommand.Unsubscribe, override val throwable: Throwable?) :
            Unsubscribed(source.topic), Failure<MqttCommand.Unsubscribe>
    }
}
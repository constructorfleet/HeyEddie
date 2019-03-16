package rocks.teagantotally.heartofgoldnotifications.domain.models.events

import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand

sealed class SubscriptionEvent(val topic: String) : Event {
    open class Subscribed(topic: String) : SubscriptionEvent(topic) {
        class Failed(override val source: MqttCommand.Subscribe, override val throwable: Throwable?) :
            Subscribed(source.topic), Failure<MqttCommand.Subscribe>
    }

    open class Unsubscribed(topic: String) : SubscriptionEvent(topic) {
        class Failed(override val source: MqttCommand.Unsubscribe, override val throwable: Throwable?) :
            Unsubscribed(source.topic), Failure<MqttCommand.Unsubscribe>
    }
}
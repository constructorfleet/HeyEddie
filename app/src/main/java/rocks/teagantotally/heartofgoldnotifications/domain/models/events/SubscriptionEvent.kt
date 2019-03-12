package rocks.teagantotally.heartofgoldnotifications.domain.models.events

sealed class SubscriptionEvent(val topic: String) : Event {
    class Subscribed(topic: String): SubscriptionEvent(topic)
    class Unsubscribed(topic: String): SubscriptionEvent(topic)
}
package rocks.teagantotally.heartofgoldnotifications.domain.framework.managers

import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.SubscriptionConfiguration


interface SubscriptionManager {
    interface Listener {
        fun onSubcriptionAdded(subscription: SubscriptionConfiguration)

        fun onSubscriptionRemoved(subscription: SubscriptionConfiguration)
    }

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    fun addSubscription(subscription: SubscriptionConfiguration)

    fun removeSubscription(subscription: SubscriptionConfiguration)

    fun getSubscriptions(): Set<SubscriptionConfiguration>

    fun getSubscriptionsFor(topic: String): Set<SubscriptionConfiguration>
}
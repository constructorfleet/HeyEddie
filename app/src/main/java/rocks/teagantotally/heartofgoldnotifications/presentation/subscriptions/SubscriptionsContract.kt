package rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions

import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.SubscriptionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView

interface SubscriptionsContract {
    interface View : BaseView<Presenter> {
        fun displaySubscription(subscription: SubscriptionConfiguration)

        fun removeSubscription(subscription: SubscriptionConfiguration)
    }

    interface Presenter : BasePresenter {
        fun addSubscription(
            topic: String,
            maxQoS: Int,
            messageType: MessageType
        )

        fun removeSubscription(
            topic: String,
            maxQoS: Int,
            messageType: MessageType
        )
    }
}
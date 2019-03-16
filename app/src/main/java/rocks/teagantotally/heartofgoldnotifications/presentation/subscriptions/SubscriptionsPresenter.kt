package rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions

import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.SubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.SubscriptionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.AddSubscription
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.RemoveSubscription
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter

class SubscriptionsPresenter(
    view: SubscriptionsContract.View,
    private val addSubscription: AddSubscription,
    private val removeSubscription: RemoveSubscription
) : SubscriptionsContract.Presenter,
    ScopedPresenter<SubscriptionsContract.View, SubscriptionsContract.Presenter>(view),
    SubscriptionManager.Listener {

    override fun onViewCreated() {

    }

    override fun addSubscription(topic: String, maxQoS: Int, messageType: MessageType) {
        launch {
            addSubscription(
                SubscriptionConfiguration(
                    topic,
                    maxQoS,
                    messageType
                )
            )
        }
    }

    override fun removeSubscription(topic: String, maxQoS: Int, messageType: MessageType) {
        launch {
            removeSubscription(
                SubscriptionConfiguration(
                    topic,
                    maxQoS,
                    messageType
                )
            )
        }
    }



    override fun onDestroyView() {

    }

    override fun onSubcriptionAdded(subscription: SubscriptionConfiguration) {
        view.displaySubscription(subscription)
    }

    override fun onSubscriptionRemoved(subscription: SubscriptionConfiguration) {
        view.removeSubscription(subscription)
    }
}
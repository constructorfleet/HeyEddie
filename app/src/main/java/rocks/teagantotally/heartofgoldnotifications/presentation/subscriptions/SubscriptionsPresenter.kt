package rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions

import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.SubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.SubscriptionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.SubscribeTo
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.UnsubscribeFrom
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter

class SubscriptionsPresenter(
    view: SubscriptionsContract.View,
    private val subscriptionManager: SubscriptionManager,
    private val subscribeTo: SubscribeTo,
    private val unsubscribeFrom: UnsubscribeFrom
) : SubscriptionsContract.Presenter,
    ScopedPresenter<SubscriptionsContract.View, SubscriptionsContract.Presenter>(view),
    SubscriptionManager.Listener {

    override fun onViewCreated() {
        subscriptionManager.getSubscriptions()
            .forEach { view.displaySubscription(it.toViewModel()) }
        subscriptionManager.addListener(this)
    }

    override fun onCreateSubscription() {
        view.showNewSubscription()
    }

    override fun addSubscription(topic: String, maxQoS: Int, messageType: MessageType) {
        SubscriptionConfiguration(
            topic,
            maxQoS,
            messageType
        ).let {
            subscriptionManager.addSubscription(
                SubscriptionConfiguration(
                    topic,
                    maxQoS,
                    messageType
                )
            )
            view.newSubscriptionSaved(it.toViewModel())
        }.run {
            launch {
                subscribeTo(MqttCommand.Subscribe(topic, maxQoS))
            }
        }
    }

    override fun removeSubscription(topic: String, maxQoS: Int, messageType: MessageType) {
        subscriptionManager.removeSubscription(
            SubscriptionConfiguration(
                topic,
                maxQoS,
                messageType
            )
        ).run {
            launch {
                unsubscribeFrom(MqttCommand.Unsubscribe(topic))
            }
        }
    }

    override fun onDestroyView() {
        subscriptionManager.removeListener(this)
    }

    override fun onSubcriptionAdded(subscription: SubscriptionConfiguration) {
        view.displaySubscription(subscription.toViewModel())
    }

    override fun onSubscriptionRemoved(subscription: SubscriptionConfiguration) {
        view.removeSubscription(subscription.toViewModel())
    }

    fun SubscriptionConfiguration.toViewModel() =
            SubscriptionViewModel.ActiveSubscription(
                topic,
                maxQoS,
                messageType
            )
}
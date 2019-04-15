package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.subscriptions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientContainer
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifMaybe
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.SubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.SubscriptionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.SubscribeTo
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.UnsubscribeFrom
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.subscriptions.viewmodels.SubscriptionViewModel
import rocks.teagantotally.kotqtt.domain.framework.client.CommandResult
import rocks.teagantotally.kotqtt.domain.framework.client.MqttEventProducer
import rocks.teagantotally.kotqtt.domain.models.QoS
import rocks.teagantotally.kotqtt.domain.models.commands.MqttSubscribeCommand
import rocks.teagantotally.kotqtt.domain.models.commands.MqttUnsubscribeCommand

class SubscriptionsPresenter(
    view: SubscriptionsContract.View,
    private val subscriptionManager: SubscriptionManager,
    coroutineScope: CoroutineScope
) : SubscriptionsContract.Presenter,
    ScopedPresenter<SubscriptionsContract.View, SubscriptionsContract.Presenter>(view, coroutineScope),
    SubscriptionManager.Listener {

    val clientContainer: ClientContainer?
        get() = HeyEddieApplication.clientComponent?.provideClientContainer()

    val subscribeTo: SubscribeTo?
        get() = clientContainer?.subscribeTo
    val unsubscribeFrom: UnsubscribeFrom?
        get() = clientContainer?.unsubscribeFrom
    val eventProducer: MqttEventProducer?
        get() = clientContainer?.eventProducer

    override fun onViewCreated() {
        subscriptionManager.getSubscriptions()
            .forEach { view.displaySubscription(it.toViewModel()) }
        subscriptionManager.addListener(this)
    }

    override fun onDeleteSubscription(subscription: SubscriptionViewModel.ActiveSubscription) {
        view.promptToDelete(subscription)
    }

    override fun onCancelNewSubscription(subscription: SubscriptionViewModel.NewSubscription) {
        view.promptToCancel(subscription)
    }

    override fun onShowCreateNewSubscription() {
        view.showCreateNewSubscription()
    }

    override fun saveNewSubscription(topic: String, maxQoS: Int, messageType: MessageType) {
        val subscription = SubscriptionConfiguration(
            topic,
            maxQoS,
            messageType
        )
        launch {
            view.showLoading()
            subscribeTo?.invoke(
                MqttSubscribeCommand(
                    topic,
                    QoS.fromQoS(maxQoS)
                )
            )
            eventProducer
                ?.subscribe()
                ?.run {
                    while (!isClosedForReceive) {
                        consumeEach { event ->
                            (event as? CommandResult<*>)
                                ?.ifMaybe({ it.command is MqttSubscribeCommand }) {
                                    when (it) {
                                        is CommandResult.Success<*, *> ->
                                            subscriptionManager.addSubscription(subscription)
                                                .run { view.newSubscriptionSaved() }
                                        is CommandResult.Failure<*> -> view.showError(it.throwable.message)
                                    }
                                }
                                ?.run { view.showLoading(false) }
                        }
                    }
                }
        }
    }

    override fun removeSubscription(topic: String, maxQoS: Int, messageType: MessageType) {
        val subscription = SubscriptionConfiguration(
            topic,
            maxQoS,
            messageType
        )
        launch {
            view.showLoading()
            unsubscribeFrom?.invoke(
                MqttUnsubscribeCommand(
                    topic
                )
            )
            eventProducer?.subscribe()
                ?.run {
                    while (!isClosedForReceive) {
                        consumeEach { event ->
                            (event as? CommandResult<*>)
                                ?.ifMaybe({ it.command is MqttUnsubscribeCommand }) {
                                    when (it) {
                                        is CommandResult.Success<*, *> ->
                                            subscriptionManager.removeSubscription(subscription)
                                        is CommandResult.Failure<*> -> view.showError(it.throwable.message)
                                    }
                                }
                                ?.let { cancel() }
                                ?.run { view.showLoading(false) }
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {
        subscriptionManager.removeListener(this)
    }

    override fun onSubscriptionAdded(subscription: SubscriptionConfiguration) {
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
package rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.connected

import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientContainer
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.mqtt.MqttConnectedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.SubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.SubscribeTo
import rocks.teagantotally.kotqtt.domain.models.QoS
import rocks.teagantotally.kotqtt.domain.models.commands.MqttSubscribeCommand

class SubscribeOnConnectUseCase(
    private val subscriptionManager: SubscriptionManager
) : MqttConnectedUseCase {
    private val clientContainer: ClientContainer
        get() = HeyEddieApplication.clientComponent.provideClientContainer()
    private val subscribeTo: SubscribeTo
        get() = clientContainer.subscribeTo

    override suspend fun invoke(parameter: Connection) {
        if (parameter is Connection.Reconnect) {
            return
        }

        subscriptionManager
            .getSubscriptions()
            .forEach {
                subscribeTo(
                    MqttSubscribeCommand(
                        it.topic,
                        QoS.fromQoS(it.maxQoS)
                    )
                )
            }
    }
}
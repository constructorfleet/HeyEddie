package rocks.teagantotally.heartofgoldnotifications.app.injection.client

import rocks.teagantotally.heartofgoldnotifications.domain.usecases.ConnectClient
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.DisconnectClient
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.publish.PublishMessage
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.SubscribeTo
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.UnsubscribeFrom
import rocks.teagantotally.kotqtt.domain.framework.client.MqttCommandExecutor
import rocks.teagantotally.kotqtt.domain.framework.client.MqttEventProducer

import javax.inject.Inject

data class ClientContainer @Inject constructor(
    var subscribeTo: SubscribeTo,
    var unsubscribeFrom: UnsubscribeFrom,
    var eventProducer: MqttEventProducer,
    var commandExecutor: MqttCommandExecutor,
    var connectClient: ConnectClient,
    var disconnectClient: DisconnectClient,
    var publishMessage: PublishMessage
)
package rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt

import com.github.ajalt.timberkt.Timber
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.models.ClientState
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.UpdatePersistentNotificationUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.connected.MqttConnectedProcessor
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.disconnected.MqttDisconnectedProcessor
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.message.receive.ProcessMessageReceived
import rocks.teagantotally.kotqtt.domain.framework.client.CommandResult
import rocks.teagantotally.kotqtt.domain.models.commands.MqttConnectCommand
import rocks.teagantotally.kotqtt.domain.models.commands.MqttDisconnectCommand
import rocks.teagantotally.kotqtt.domain.models.commands.MqttSubscribeCommand
import rocks.teagantotally.kotqtt.domain.models.commands.MqttUnsubscribeCommand
import rocks.teagantotally.kotqtt.domain.models.events.*

class MqttEventProcessor(
    private val updatePersistentNotification: UpdatePersistentNotificationUseCase,
    private val onDisconnected: MqttDisconnectedProcessor,
    private val onConnected: MqttConnectedProcessor,
    private val processMessage: ProcessMessageReceived
) : UseCaseWithParameter<MqttEvent> {
    override suspend fun invoke(parameter: MqttEvent) {
        val receivedEvent = (parameter as? CommandResult.Success<*, *>)?.let { it.result } ?: parameter
        when (receivedEvent) {
            MqttConnectedEvent ->
                onConnected(MqttConnectedEvent)
            is MqttDisconnectedEvent ->
                onDisconnected(receivedEvent)
            is CommandResult.Failure<*> ->
                when (receivedEvent.command) {
                    is MqttConnectCommand, is MqttDisconnectCommand ->
                        updatePersistentNotification(ClientState.Unknown)
                    is MqttSubscribeCommand ->
                        Timber.e(receivedEvent.throwable) { "Cannot subscribe" }
                    is MqttUnsubscribeCommand ->
                        Timber.e(receivedEvent.throwable) { "Cannot unsubscribe" }
                }
            is MqttSubscribedEvent ->
                Timber.d { "Subscribed to ${receivedEvent.topic}" }
            is MqttUnsubscribedEvent ->
                Timber.d { "Unsubscribed from ${receivedEvent.topic}" }
            is MqttMessageReceived -> processMessage(receivedEvent.message)
//                is MqttEvent.MessagePublished -> processMessagePublished(event)
            else -> null
        }
    }
}
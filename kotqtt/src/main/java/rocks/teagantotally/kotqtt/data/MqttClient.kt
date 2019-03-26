package rocks.teagantotally.kotqtt.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*
import rocks.teagantotally.kotqtt.domain.framework.client.Client
import rocks.teagantotally.kotqtt.domain.framework.client.CommandResult
import rocks.teagantotally.kotqtt.domain.framework.connections.MqttAuthentication
import rocks.teagantotally.kotqtt.domain.framework.connections.MqttConnectionOptions
import rocks.teagantotally.kotqtt.domain.models.Message
import rocks.teagantotally.kotqtt.domain.models.QoS
import rocks.teagantotally.kotqtt.domain.models.commands.*
import rocks.teagantotally.kotqtt.domain.models.events.*
import java.util.*

class MqttClient(
    private val client: IMqttAsyncClient,
    private val connectionOptions: MqttConnectionOptions,
    coroutineScope: CoroutineScope
) : Client,
    MqttCallback,
    CoroutineScope by coroutineScope {

    private val commandChannel: ReceiveChannel<MqttCommand> = Channel()
    private val eventChannel: BroadcastChannel<MqttEvent> = ConflatedBroadcastChannel()

    override suspend fun execute(command: MqttCommand) {
        if (!commandChannel.isClosedForReceive) {
            commandChannel.consumeEach {
                when (it) {
                    is MqttConnectCommand -> connect(it)
                    is MqttDisconnectCommand -> disconnect(it)
                    is MqttPublishCommand -> publish(it)
                    is MqttSubscribeCommand -> subscribe(it)
                    is MqttUnsubscribeCommand -> unsubscribe(it)
                }
            }
        }
    }

    override fun subscribe(): ReceiveChannel<MqttEvent> =
        eventChannel.openSubscription()

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        topic
            ?.let {
                message?.run {
                    launch {
                        eventChannel.send(
                            MqttMessageReceived(
                                Message(
                                    it,
                                    isRetained,
                                    QoS.fromQoS(qos),
                                    payload
                                ),
                                Date()
                            )
                        )
                    }
                }
            }
    }

    override fun connectionLost(throwable: Throwable?) {
        sendEvent(
            MqttDisconnectedEvent(throwable != null)
        )
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        // no-op
    }

    private fun connect(command: MqttConnectCommand) {
        client.connect(
            connectionOptions.transform(),
            null,
            getListener(
                command,
                MqttConnectedEvent
            )
        )
    }

    private fun disconnect(command: MqttDisconnectCommand) {
        if (!client.isConnected) {
            sendNotConnectedEvent(command)
        } else {
            client.disconnect(
                null,
                getListener(
                    command,
                    MqttDisconnectedEvent()
                )
            )
        }
    }

    private fun publish(command: MqttPublishCommand) {
        if (!client.isConnected) {
            sendNotConnectedEvent(command)
        } else {
            with(command.message) {
                client.publish(
                    topic,
                    payload,
                    qos.value,
                    retain,
                    null,
                    getListener(
                        command,
                        MqttMessagePublished(
                            command.message,
                            Date()
                        )
                    )
                )
            }
        }

    }

    private fun subscribe(command: MqttSubscribeCommand) {
        if (!client.isConnected) {
            sendNotConnectedEvent(command)
        } else {
            with(command) {
                client.subscribe(
                    topic,
                    qos.value,
                    null,
                    getListener(
                        command,
                        MqttSubscribedEvent(
                            command.topic
                        )
                    )
                )
            }
        }
    }

    private fun unsubscribe(command: MqttUnsubscribeCommand) {
        if (!client.isConnected) {
            sendNotConnectedEvent(command)
        } else {
            with(command) {
                client.unsubscribe(
                    topic,
                    null,
                    getListener(
                        command,
                        MqttUnsubscribedEvent(
                            command.topic
                        )
                    )
                )
            }
        }
    }

    private fun <CommandType : MqttCommand> sendNotConnectedEvent(command: CommandType) {
        sendEvent(
            CommandResult.Failure(
                command,
                IllegalStateException("Client is not connected")
            )
        )
    }

    private fun <EventType : MqttEvent> sendEvent(event: EventType) {
        launch {
            if (!eventChannel.isClosedForSend) {
                eventChannel.send(event)
            }
        }
    }

    private fun <MqttCommandType : MqttCommand, MqttEventType : MqttEvent>
            getListener(command: MqttCommandType, successEvent: MqttEventType): IMqttActionListener =
        object : IMqttActionListener {
            override fun onSuccess(token: IMqttToken?) {
                sendEvent(
                    CommandResult.Success(
                        command,
                        successEvent
                    )
                )
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                sendEvent(
                    CommandResult.Failure(
                        command,
                        throwable ?: UnknownError()
                    )
                )
            }
        }

    private fun MqttConnectionOptions.transform(): MqttConnectOptions =
        MqttConnectOptions().also {
            it.keepAliveInterval = keepAliveInterval
            it.isCleanSession = cleanSession
            it.isAutomaticReconnect = reconnect
            it.mqttVersion =
                when (useVersion31) {
                    true -> 4
                    false -> 3
                }
            lastWill
                ?.let { will ->
                    it.setWill(
                        will.topic,
                        will.payload,
                        will.qos.value,
                        will.retain
                    )
                }
            (authentication as? MqttAuthentication.Basic)
                ?.let { auth ->
                    it.userName = auth.username
                    it.password = auth.password.toCharArray()
                }
        }
}
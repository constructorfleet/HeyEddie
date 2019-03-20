package rocks.teagantotally.heartofgoldnotifications.domain.clients

import kotlinx.coroutines.*
import org.eclipse.paho.client.mqttv3.*
import rocks.teagantotally.heartofgoldnotifications.common.extensions.safeLet
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.MqttEventConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MqttEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.onPublish
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import java.util.*
import kotlin.coroutines.CoroutineContext

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MqttClient(
    private val client: IMqttAsyncClient,
    private val connectionConfigManager: ConnectionConfigManager,
    private val mqttEventProcessor: MqttEventConsumer
) : Client, Scoped {

    companion object {
        private val DEFAULT_ERROR = Throwable("An unknown error occurred")
    }

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext =
        job.plus(Dispatchers.IO)

    private fun <MqttCommandType : MqttCommand, MqttEventType : MqttEvent>
            getListener(command: MqttCommandType, successEvent: MqttEventType): IMqttActionListener =
        object : IMqttActionListener {
            override fun onSuccess(token: IMqttToken?) {
                mqttEventProcessor.consume(successEvent)
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                mqttEventProcessor.consume(
                    MqttEvent.CommandFailed(
                        command,
                        throwable ?: DEFAULT_ERROR
                    )
                )
            }
        }

    init {
        client.setCallback(this)
    }

    override fun isConnected(): Boolean =
        client.isConnected

    override fun connect() {
        if (client.isConnected || !connectionConfigManager.hasConnectionConfiguration()) {
            return
        }

        launch {
            try {
                client.connect(
                    connectionConfigManager.getConnectionConfiguration()!!.transform(),
                    null,
                    getListener(MqttCommand.Connect, MqttEvent.Connected)
                )
            } catch (throwable: Throwable) {
                mqttEventProcessor.consume(
                    MqttEvent.CommandFailed(
                        MqttCommand.Connect,
                        throwable
                    )
                )
            }
        }
    }

    override fun disconnect() {
        launch {
            try {
                client.disconnect(
                    null,
                    getListener(MqttCommand.Disconnect, MqttEvent.Disconnected)
                )
            } catch (throwable: Throwable) {
                mqttEventProcessor.consume(
                    MqttEvent.CommandFailed(
                        MqttCommand.Disconnect,
                        throwable
                    )
                )
            }
        }
    }

    override fun connectComplete(reconnect: Boolean, brokerUri: String?) {
    }

    override fun publish(message: Message) {
        launch {
            try {
                client.publish(
                    message.topic,
                    MqttMessage(message.payload.toByteArray())
                        .apply {
                            isRetained = message.retain
                            qos = message.qos
                        },
                    null,
                    getListener(
                        MqttCommand.Publish(message),
                        MqttEvent.MessagePublished(message.onPublish())
                    )
                )
            } catch (throwable: Throwable) {
                mqttEventProcessor.consume(
                    MqttEvent.CommandFailed(
                        MqttCommand.Publish(message),
                        throwable
                    )
                )
            }
        }
    }

    override fun subscribe(topic: String, qosMax: Int) {
        launch {
            MqttCommand.Subscribe(topic, qosMax)
                .let { command ->
                    client.subscribe(
                        topic,
                        qosMax,
                        null,
                        getListener(
                            command,
                            MqttEvent.Subscribed(topic)
                        )
                    )
                }
        }
    }

    override fun unsubscribe(topic: String) {
        launch {
            MqttCommand.Unsubscribe(topic)
                .let { command ->
                    client.unsubscribe(
                        topic,
                        null,
                        getListener(
                            command,
                            MqttEvent.Unsubscribed(topic)
                        )
                    )
                }
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        launch {
            safeLet(topic, message) { validTopic, validMessage ->
                mqttEventProcessor.consume(
                    MqttEvent.MessageReceived(
                        Message(
                            validTopic,
                            String(validMessage.payload),
                            validMessage.qos,
                            validMessage.isRetained,
                            Date()
                        )
                    )
                )
            }
        }
    }

    override fun connectionLost(throwable: Throwable?) {
        launch {
            mqttEventProcessor.consume(MqttEvent.Disconnected)
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        // no-op
    }
}
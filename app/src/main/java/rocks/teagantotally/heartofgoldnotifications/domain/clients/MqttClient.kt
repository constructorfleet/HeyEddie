package rocks.teagantotally.heartofgoldnotifications.domain.clients

import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.eclipse.paho.client.mqttv3.*
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.*
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import kotlin.coroutines.CoroutineContext

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MqttClient(
    private val client: IMqttAsyncClient,
    private val connectionConfigProvider: ConnectionConfigProvider,
    private val eventChannel: SendChannel<Event>,
    private val commandChannel: ReceiveChannel<CommandEvent>
) : Client, Scoped {

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext =
        job.plus(Dispatchers.IO)

    private val connectionListener: IMqttActionListener =
        object : IMqttActionListener {

            override fun onSuccess(token: IMqttToken?) {
                launch {
                    if (!eventChannel.isClosedForSend) {
                        listenForMessages()
                        eventChannel.send(
                            ClientConnection.Successful(
                                token
                            )
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                launch {
                    if (!eventChannel.isClosedForSend) {
                        eventChannel.send(
                            ClientConnection.Failed(
                                token,
                                throwable ?: Throwable("Unable to connect to broker")
                            )
                        )
                    }
                }
            }
        }

    private fun getUnsubscribeListener(topic: String): IMqttActionListener =
        object : IMqttActionListener {
            val topic: String = topic
            override fun onSuccess(token: IMqttToken?) {
                launch {
                    if (!eventChannel.isClosedForSend) {
                        eventChannel.send(
                            ClientUnsubscribe.Successful(
                                token,
                                topic
                            )
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                launch {
                    if (!eventChannel.isClosedForSend) {
                        eventChannel.send(
                            ClientUnsubscribe.Failed(
                                token,
                                topic,
                                throwable ?: Throwable("Unable to subscribe to $topic")
                            )
                        )
                    }
                }
            }
        }

    private fun getSubscribeListener(topic: String, maxQoS: Int): IMqttActionListener =
        object : IMqttActionListener, CoroutineScope by this@MqttClient {
            val topic: String = topic
            val maxQoS: Int = maxQoS

            override fun onSuccess(token: IMqttToken?) {
                launch {
                    if (!eventChannel.isClosedForSend) {
                        eventChannel.send(
                            ClientSubscription.Successful(
                                token,
                                topic
                            )
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                launch {
                    if (!eventChannel.isClosedForSend) {
                        eventChannel.send(
                            ClientSubscription.Failed(
                                token,
                                topic,
                                throwable ?: Throwable("Unable to unsubscribe from $topic")
                            )
                        )
                    }
                }
            }
        }

    private val disconnectListener: IMqttActionListener =
        object : IMqttActionListener, CoroutineScope by this@MqttClient {
            override fun onSuccess(token: IMqttToken?) {
                launch {
                    if (!eventChannel.isClosedForSend) {
                        eventChannel.send(
                            ClientDisconnection.Successful(token)
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                launch {
                    if (!eventChannel.isClosedForSend) {
                        eventChannel.send(
                            ClientDisconnection.Failed(
                                token,
                                throwable ?: Throwable("Unable to disconnect")
                            )
                        )
                    }
                }
            }
        }

    private fun getPublishListener(message: Message): IMqttActionListener =
        object : IMqttActionListener, CoroutineScope by this@MqttClient {

            override fun onSuccess(token: IMqttToken?) {
                launch {
                    if (!eventChannel.isClosedForSend) {
                        eventChannel.send(
                            ClientMessagePublish.Successful(
                                token,
                                message
                            )
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                launch {
                    if (!eventChannel.isClosedForSend) {
                        eventChannel.send(
                            ClientMessagePublish.Failed(
                                token,
                                message,
                                throwable ?: Throwable("Unable to unsubscribe from ${message.topic}")
                            )
                        )
                    }
                }
            }
        }

    init {
        client.setCallback(this)
    }

    override fun connect() {
        if (client.isConnected || !connectionConfigProvider.hasConnectionConfiguration()) {
            return
        }

        launch {
            client.connect(
                connectionConfigProvider.getConnectionConfiguration().transform(),
                null,
                connectionListener
            )
        }
    }

    override fun disconnect() {
        launch {
            client.disconnect(null, disconnectListener)
        }
    }

    override fun connectComplete(reconnect: Boolean, brokerUri: String?) {
        subscribe("/test", 0)
    }

    override fun publish(message: Message) {
        launch {
            try {
                if (!client.isConnected) {
                    throw IllegalStateException("Client is not connected")
                }
                client.publish(
                    message.topic,
                    MqttMessage(message.payload.toByteArray())
                        .apply {
                            isRetained = message.retain
                            qos = message.qos
                        },
                    null,
                    getPublishListener(message)
                )
            } catch (t: Throwable) {
                eventChannel.send(
                    ClientMessagePublish.Failed(
                        null,
                        message,
                        t
                    )
                )
            }
        }
    }

    override fun subscribe(topic: String, qosMax: Int) {
        launch {
            if (!client.isConnected) {
                eventChannel.send(
                    ClientSubscription.Failed(
                        null,
                        topic,
                        IllegalStateException("Client is not connected")
                    )
                )
            } else {
                client.subscribe(topic, qosMax, null, getSubscribeListener(topic, qosMax))
            }
        }
    }

    override fun unsubscribe(topic: String) {
        launch {
            if (!client.isConnected) {
                eventChannel.send(
                    ClientUnsubscribe.Failed(
                        null,
                        topic,
                        IllegalStateException("Client is not connected")
                    )
                )
            } else {
                client.unsubscribe(topic, null, getUnsubscribeListener(topic))
            }
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        launch {
            Timber.d { "Received Message" }
            eventChannel.send(
                topic?.let { validTopic ->
                    message?.let { validMessage ->
                        ClientMessageReceive.Successful(
                            Message(
                                validTopic,
                                String(validMessage.payload),
                                validMessage.qos,
                                validMessage.isRetained
                            )
                        )
                    } ?: ClientMessageReceive.Failed(Throwable("Empty message"))
                } ?: ClientMessageReceive.Failed(Throwable("Empty topic"))
            )
        }
    }

    override fun connectionLost(throwable: Throwable?) {
        launch {
            eventChannel.send(
                ClientConnection.Failed(
                    null,
                    throwable ?: Throwable("Connection lost")
                )
            )
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        // no-op
    }

    @ObsoleteCoroutinesApi
    private fun listenForMessages() {
        launch {
            while (client.isConnected) {
                commandChannel.consumeEach {
                    when (it) {
                        CommandEvent.Disconnect -> disconnect()
                        CommandEvent.Connect -> connect()
                        CommandEvent.GetStatus ->
                            if (!eventChannel.isClosedForSend) {
                                eventChannel.send(
                                    ClientStatus(client.isConnected)
                                )
                            }
                        is CommandEvent.Publish -> publish(it.message)
                        is CommandEvent.Subscribe -> subscribe(it.topic, it.maxQoS)
                        is CommandEvent.Unsubscribe -> unsubscribe(it.topic)
                    }
                }
            }
        }
    }
}
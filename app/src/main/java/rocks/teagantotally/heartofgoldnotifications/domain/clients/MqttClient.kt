package rocks.teagantotally.heartofgoldnotifications.domain.clients

import com.github.ajalt.timberkt.Timber
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import org.eclipse.paho.client.mqttv3.*
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.data.services.NotificationService
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.*
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MqttClient(
    private val client: IMqttAsyncClient,
    private val connectionConfigProvider: ConnectionConfigProvider,
    private val clientEventChannel: SendChannel<ClientEvent>,
    private val commandChannel: Channel<CommandEvent>
) : Client, Scoped {

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext =
        job.plus(Dispatchers.IO)

    private var connectionToken: IMqttToken? = null
    private var isListening: Boolean = false

    private val connectionListener: IMqttActionListener =
        object : IMqttActionListener {

            override fun onSuccess(token: IMqttToken?) {
                launch {
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
                            ClientConnection.Successful(
                                token
                            )
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                launch {
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
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
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
                            ClientUnsubscription.Successful(
                                token,
                                topic
                            )
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                launch {
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
                            ClientUnsubscription.Failed(
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
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
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
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
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
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
                            ClientDisconnection.Successful(token)
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                launch {
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
                            ClientDisconnection.Failed(
                                token,
                                throwable ?: Throwable("Unable to disconnect")
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
        if (client.isConnected) {
            return
        }

        isListening = false
        client.connect(
            connectionConfigProvider.getConnectionConfiguration().transform(),
            null,
            connectionListener
        )
    }

    override fun disconnect() {
        client.disconnect(null, disconnectListener)
    }

    override fun connectComplete(reconnect: Boolean, brokerUri: String?) {
        listenForMessages()
    }

    override fun publish(message: Message) {
        launch {
            try {
                client.publish(
                    message.topic,
                    message.payload.toByteArray(),
                    message.qos,
                    message.retain
                ).let {
                    clientEventChannel.send(
                        ClientMessagePublish.Successful(
                            it,
                            message
                        )
                    )
                }
            } catch (t: Throwable) {
                clientEventChannel.send(
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
        client.subscribe(topic, qosMax, null, getSubscribeListener(topic, qosMax))
    }

    override fun unsubscribe(topic: String) {
        client.unsubscribe(topic, null, getUnsubscribeListener(topic))
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        launch {
            Timber.d { "Received Message" }
            clientEventChannel.send(
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
            clientEventChannel.send(
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
        if (isListening) {
            return
        }

        launch {
            isListening = true
            while (client.isConnected && isListening) {
                commandChannel.consumeEach {
                    when (it) {
                        CommandEvent.Disconnect -> disconnect()
                        CommandEvent.Connect -> connect()
                        CommandEvent.GetStatus ->
                            if (!clientEventChannel.isClosedForSend) {
                                clientEventChannel.send(
                                    ClientStatus(client.isConnected)
                                )
                            }
                        is CommandEvent.Publish -> publish(it.message)
                        is CommandEvent.Subscribe -> subscribe(it.topic, it.maxQoS)
                        is CommandEvent.Unsubscribe -> unsubscribe(it.topic)
                    }
                }
            }

            isListening = false
        }
    }
}
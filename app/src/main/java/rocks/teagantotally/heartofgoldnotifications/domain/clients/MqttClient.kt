package rocks.teagantotally.heartofgoldnotifications.domain.clients

import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import org.eclipse.paho.client.mqttv3.*
import rocks.teagantotally.heartofgoldnotifications.domain.framework.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.ClientCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.ConnectionCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ConnectionEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MessageEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.SubscriptionEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.EmptyMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import kotlin.coroutines.CoroutineContext

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MqttClient(
    private val client: IMqttAsyncClient,
    private val connectionConfigProvider: ConnectionConfigProvider,
    private val connectionEventChannel: SendChannel<ConnectionEvent>,
    private val messageEventChannel: SendChannel<MessageEvent>,
    private val subscriptionEventChannel: SendChannel<SubscriptionEvent>,
    private val connectionCommandChannel: ReceiveChannel<ConnectionCommand>,
    private val clientCommandChannel: ReceiveChannel<ClientCommand>
) : Client, Scoped {

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext =
        job.plus(Dispatchers.IO)

    private fun getConnectionListener(command: ConnectionCommand): IMqttActionListener =
        object : IMqttActionListener {

            override fun onSuccess(token: IMqttToken?) {
                launch {
                    if (!connectionEventChannel.isClosedForSend) {
                        connectionEventChannel.send(
                            ConnectionEvent.Connected
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                launch {
                    if (!connectionEventChannel.isClosedForSend) {
                        connectionEventChannel.send(
                            ConnectionEvent.Failed(
                                command,
                                throwable
                            )
                        )
                    }
                }
            }
        }

    private fun getUnsubscribeListener(command: ClientCommand.Unsubscribe): IMqttActionListener =
        object : IMqttActionListener {
            override fun onSuccess(token: IMqttToken?) {
                launch {
                    if (!subscriptionEventChannel.isClosedForSend) {
                        subscriptionEventChannel.send(
                            SubscriptionEvent.Unsubscribed(command.topic)
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                launch {
                    if (!subscriptionEventChannel.isClosedForSend) {
                        subscriptionEventChannel.send(
                            SubscriptionEvent.Unsubscribed.Failed(
                                command,
                                throwable
                            )
                        )
                    }
                }
            }
        }

    private fun getSubscribeListener(command: ClientCommand.SubscribeTo): IMqttActionListener =
        object : IMqttActionListener, CoroutineScope by this@MqttClient {

            override fun onSuccess(token: IMqttToken?) {
                launch {
                    if (!subscriptionEventChannel.isClosedForSend) {
                        subscriptionEventChannel.send(
                            SubscriptionEvent.Subscribed(command.topic)
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                launch {
                    if (!subscriptionEventChannel.isClosedForSend) {
                        subscriptionEventChannel.send(
                            SubscriptionEvent.Subscribed.Failed(
                                command,
                                throwable
                            )
                        )
                    }
                }
            }
        }

    private fun getDisconnectListener(command: ConnectionCommand.Disconnect): IMqttActionListener =
        object : IMqttActionListener, CoroutineScope by this@MqttClient {
            override fun onSuccess(token: IMqttToken?) {
                launch {
                    if (!connectionEventChannel.isClosedForSend) {
                        connectionEventChannel.send(
                            ConnectionEvent.Disconnected
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                launch {
                    if (!connectionEventChannel.isClosedForSend) {
                        connectionEventChannel.send(
                            ConnectionEvent.Failed(
                                command,
                                throwable
                            )
                        )
                    }
                }
            }
        }

    private fun getPublishListener(command: ClientCommand.Publish): IMqttActionListener =
        object : IMqttActionListener, CoroutineScope by this@MqttClient {

            override fun onSuccess(token: IMqttToken?) {
                launch {
                    if (!messageEventChannel.isClosedForSend) {
                        messageEventChannel.send(
                            MessageEvent.Published(command.message)
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                launch {
                    if (!messageEventChannel.isClosedForSend) {
                        messageEventChannel.send(
                            MessageEvent.Published.Failed(
                                command,
                                throwable
                            )
                        )
                    }
                }
            }
        }

    init {
        client.setCallback(this)
    }

    override fun isConnected(): Boolean =
        client.isConnected

    override fun connect() {
        if (client.isConnected || !connectionConfigProvider.hasConnectionConfiguration()) {
            return
        }

        launch {
            client.connect(
                connectionConfigProvider.getConnectionConfiguration().transform(),
                null,
                getConnectionListener(ConnectionCommand.Connect)
            )
        }
    }

    override fun disconnect() {
        launch {
            client.disconnect(null, getDisconnectListener(ConnectionCommand.Disconnect))
        }
    }

    override fun connectComplete(reconnect: Boolean, brokerUri: String?) {
        listenForMessages()
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
                    getPublishListener(ClientCommand.Publish(message))
                )
            } catch (throwable: Throwable) {
                messageEventChannel.send(
                    MessageEvent.Published.Failed(
                        ClientCommand.Publish(
                            message
                        ),
                        throwable
                    )
                )
            }
        }
    }

    override fun subscribe(topic: String, qosMax: Int) {
        launch {
            ClientCommand.SubscribeTo(topic, qosMax)
                .let { command ->
                    if (!client.isConnected) {
                        subscriptionEventChannel.send(
                            SubscriptionEvent.Subscribed.Failed(
                                command,
                                Throwable("Client is not connected")
                            )
                        )
                    } else {
                        client.subscribe(topic, qosMax, null, getSubscribeListener(command))
                    }
                }
        }
    }

    override fun unsubscribe(topic: String) {
        launch {
            ClientCommand.Unsubscribe(topic)
                .let { command ->
                    if (!client.isConnected) {
                        subscriptionEventChannel.send(
                            SubscriptionEvent.Unsubscribed.Failed(
                                command,
                                Throwable("Client is not connected")
                            )
                        )
                    } else {
                        client.unsubscribe(topic, null, getUnsubscribeListener(command))
                    }
                }
        }
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        launch {
            Timber.d { "Received Message" }
            messageEventChannel.send(
                topic?.let { validTopic ->
                    message?.let { validMessage ->
                        MessageEvent.Received(
                            Message(
                                validTopic,
                                String(validMessage.payload),
                                validMessage.qos,
                                validMessage.isRetained
                            )
                        )
                    } ?: MessageEvent.Received.Failed(EmptyMessage, Throwable("Empty message"))
                } ?: MessageEvent.Received.Failed(EmptyMessage, Throwable("Empty topic"))
            )
        }
    }

    override fun connectionLost(throwable: Throwable?) {
        launch {
            connectionEventChannel.send(
                ConnectionEvent.Disconnected
            )
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        // no-op
    }

    @ObsoleteCoroutinesApi
    private fun listenForMessages() {
        launch {
            while (true) {
                connectionCommandChannel.consumeEach {
                    when (it) {
                        ConnectionCommand.Disconnect -> disconnect()
                        ConnectionCommand.Connect -> connect()
                        ConnectionCommand.GetStatus ->
                            if (!connectionEventChannel.isClosedForSend) {
                                connectionEventChannel.send(
                                    ConnectionEvent.Status(client.isConnected)
                                )
                            }
                    }
                }
            }
        }
        launch {
            while (true)
                clientCommandChannel.consumeEach {
                    when (it) {
                        is ClientCommand.Publish -> publish(it.message)
                        is ClientCommand.SubscribeTo -> subscribe(it.topic, it.maxQoS)
                        is ClientCommand.Unsubscribe -> unsubscribe(it.topic)
                    }
                }
        }
    }
}
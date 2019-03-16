package rocks.teagantotally.heartofgoldnotifications.domain.clients

import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import org.eclipse.paho.client.mqttv3.*
import rocks.teagantotally.heartofgoldnotifications.domain.framework.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand
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
    private val subscriptionEventChannel: SendChannel<SubscriptionEvent>
) : Client, Scoped {

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext =
        job.plus(Dispatchers.IO)
    private val connectionListeners: MutableSet<Client.ConnectionListener> = mutableSetOf()

    private fun getConnectionListener(command: MqttCommand.Connect): IMqttActionListener =
        object : IMqttActionListener {

            override fun onSuccess(token: IMqttToken?) {
                notifyListeners(Client.ConnectionState.Connected)

                launch {
                    if (!connectionEventChannel.isClosedForSend) {
                        connectionEventChannel.send(
                            ConnectionEvent.Connected
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                notifyListeners(Client.ConnectionState.Error(throwable?.message ?: "Error connecting to broker"))

//                launch {
//                    if (!connectionEventChannel.isClosedForSend) {
//                        connectionEventChannel.send(
//                            ConnectionEvent.Failed(
//                                command,
//                                throwable
//                            )
//                        )
//                    }
//                }
            }
        }

    private fun getUnsubscribeListener(command: MqttCommand.Unsubscribe): IMqttActionListener =
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

    private fun getSubscribeListener(command: MqttCommand.Subscribe): IMqttActionListener =
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

    private fun getDisconnectListener(command: MqttCommand.Disconnect): IMqttActionListener =
        object : IMqttActionListener, CoroutineScope by this@MqttClient {
            override fun onSuccess(token: IMqttToken?) {
                notifyListeners(Client.ConnectionState.Disconnected)

                launch {
                    if (!connectionEventChannel.isClosedForSend) {
                        connectionEventChannel.send(
                            ConnectionEvent.Disconnected
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                notifyListeners(Client.ConnectionState.Error(throwable?.message ?: "Error disconnecting from broker"))

//                launch {
//                    if (!connectionEventChannel.isClosedForSend) {
//                        connectionEventChannel.send(
//                            ConnectionEvent.Failed(
//                                command,
//                                throwable
//                            )
//                        )
//                    }
//                }
            }
        }

    private fun getPublishListener(command: MqttCommand.Publish): IMqttActionListener =
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

    override fun addConnectionListener(listener: Client.ConnectionListener) {
        connectionListeners.add(listener)
    }

    override fun removeConnectionListener(listener: Client.ConnectionListener) {
        connectionListeners.remove(listener)
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
                getConnectionListener(MqttCommand.Connect)
            )
        }
    }

    override fun disconnect() {
        launch {
            client.disconnect(null, getDisconnectListener(MqttCommand.Disconnect))
        }
    }

    override fun connectComplete(reconnect: Boolean, brokerUri: String?) {
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
                    getPublishListener(MqttCommand.Publish(message))
                )
            } catch (throwable: Throwable) {
                messageEventChannel.send(
                    MessageEvent.Published.Failed(
                        MqttCommand.Publish(
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
            MqttCommand.Subscribe(topic, qosMax)
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
            MqttCommand.Unsubscribe(topic)
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

    private fun notifyListeners(state: Client.ConnectionState) {
        connectionListeners
            .forEach { it.onConnectionChange(state) }
    }
}
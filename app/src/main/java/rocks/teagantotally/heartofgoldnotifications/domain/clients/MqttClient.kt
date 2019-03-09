package rocks.teagantotally.heartofgoldnotifications.domain.clients

import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import org.eclipse.paho.client.mqttv3.*
import rocks.teagantotally.heartofgoldnotifications.domain.models.*
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import kotlin.coroutines.CoroutineContext

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MqttClient(
    private val client: IMqttAsyncClient,
    private val clientEventChannel: SendChannel<ClientEvent>,
    private val messageChannel: BroadcastChannel<MessageEvent>,
    private val notifyChannel: Channel<String>
) : Client, Scoped {

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext =
        job.plus(Dispatchers.IO)

    private var connectionToken: IMqttToken? = null
    private var isListening: Boolean = false

    private val connectionListener: IMqttActionListener =
        object : IMqttActionListener, CoroutineScope by this@MqttClient {

            override fun onSuccess(token: IMqttToken?) {
                Timber.d { "Connected $token" }
                launch {
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
                            ClientEvent.Success(
                                ClientEventType.Connection,
                                token
                            )
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                Timber.d { "Connect Failed $token ${throwable?.message}" }
                launch {
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
                            ClientEvent.Failed(
                                ClientEventType.Connection,
                                token,
                                throwable ?: Throwable("Unable to connect")
                            )
                        )
                    }
                }
            }
        }

    private val subscribeListener: IMqttActionListener =
        object : IMqttActionListener, CoroutineScope by this@MqttClient {
            override fun onSuccess(token: IMqttToken?) {
                Timber.d { "Subscribe $token" }
                launch {
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
                            ClientEvent.Success(
                                ClientEventType.Subscribe,
                                token
                            )
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                Timber.d { "Subscribe Failed $token ${throwable?.message}" }
                launch {
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
                            ClientEvent.Failed(
                                ClientEventType.Subscribe,
                                token,
                                throwable ?: Throwable("Unable to subscribe")
                            )
                        )
                    }
                }
            }
        }

    private val unsubscribeListener: IMqttActionListener =
        object : IMqttActionListener, CoroutineScope by this@MqttClient {
            override fun onSuccess(token: IMqttToken?) {
                Timber.d { "Unsubscribe $token" }
                launch {
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
                            ClientEvent.Success(
                                ClientEventType.Unsubscribe,
                                token
                            )
                        )
                    }
                }
            }

            override fun onFailure(token: IMqttToken?, throwable: Throwable?) {
                Timber.d { "Unsubscribe Failed $token ${throwable?.message}" }
                launch {
                    if (!clientEventChannel.isClosedForSend) {
                        clientEventChannel.send(
                            ClientEvent.Failed(
                                ClientEventType.Unsubscribe,
                                token,
                                throwable ?: Throwable("Unable to subscribe")
                            )
                        )
                    }
                }
            }
        }

    init {
        client.setCallback(this)
    }

    override fun connect(connectionConfiguration: ConnectionConfiguration) {
        Timber.d { "Connecting" }
        client.connect(connectionConfiguration.transform(), null, connectionListener)
    }

    override fun disconnect() {
        client.disconnect()
    }

    override fun connectComplete(reconnect: Boolean, brokerUri: String?) {
        listenForMessages()
    }

    override fun publish(message: ReceivedMessage) {
        launch {
            try {
                client.publish(
                    message.topic,
                    message.payload,
                    message.qos,
                    message.retain
                ).let {
                    messageChannel.send(MessageEvent.PublishResult.Success(it, message))
                }
            } catch (t: Throwable) {
                messageChannel.send(MessageEvent.PublishResult.Failure(t, message))
            }
        }
    }

    override fun subscribe(topic: String, qosMax: Int) {
        client.subscribe(topic, qosMax, null, subscribeListener)
    }

    override fun unsubscribe(topic: String) {
        client.unsubscribe(topic, null, unsubscribeListener)
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        launch {
            Timber.d { "Received ReceivedMessage" }
            messageChannel.send(
                topic?.let { validTopic ->
                    message?.let { validMessage ->
                        MessageEvent.Received.Success(
                            ReceivedMessage(
                                validTopic,
                                validMessage.payload,
                                validMessage.qos,
                                validMessage.isRetained
                            )
                        )
                    } ?: MessageEvent.Received.Failure(Throwable("Empty message"))
                } ?: MessageEvent.Received.Failure(Throwable("Empty topic"))
            )
        }
    }

    override fun connectionLost(throwable: Throwable?) {
        launch {
            clientEventChannel.send(
                ClientEvent.Failed(
                    ClientEventType.Subscribe,
                    connectionToken,
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
            while (client.isConnected) {
                isListening = true
                messageChannel.consumeEach {
                    when (it) {
                        is MessageEvent.Publish -> publish(it.message)
                    }
                }
            }

            isListening = false
        }
    }
}
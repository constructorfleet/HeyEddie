package rocks.teagantotally.heartofgoldnotifications.domain.clients

import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*
import rocks.teagantotally.heartofgoldnotifications.domain.models.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MqttClient(
    private val client: IMqttAsyncClient,
    private val clientEventChannel: SendChannel<ClientEvent>,
    private val messageChannel: Channel<MessageEvent>,
    coroutineScope: CoroutineScope
) : Client, CoroutineScope by coroutineScope {

    private var connectionToken: IMqttToken? = null
    private var isListening: Boolean = false

    private val connectionListener: IMqttActionListener =
        object : IMqttActionListener, CoroutineScope by coroutineScope {

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
        object : IMqttActionListener, CoroutineScope by coroutineScope {
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
        object : IMqttActionListener, CoroutineScope by coroutineScope {
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
            while (messageChannel.isClosedForReceive && client.isConnected) {
                isListening = true
                messageChannel.receiveOrNull()
                    ?.let {
                        when (it) {
                            is MessageEvent.Publish -> publish(it.message)
                        }
                    }
            }

            isListening = false
        }
    }
}
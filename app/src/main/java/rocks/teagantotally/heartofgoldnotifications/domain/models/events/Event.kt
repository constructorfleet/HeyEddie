package rocks.teagantotally.heartofgoldnotifications.domain.models.events

import org.eclipse.paho.client.mqttv3.IMqttToken
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message

interface Event

interface TokenizedClientEvent {
    val token: IMqttToken?
}

interface Success : Event

interface Failure : Event {
    val throwable: Throwable
}

interface ClientMessageEvent : Event

class ClientStatus(val isConnected: Boolean) : Event

sealed class ClientConnection(override val token: IMqttToken?) : TokenizedClientEvent {
    class Successful(token: IMqttToken?) : ClientConnection(token), Success
    class Failed(token: IMqttToken?, override val throwable: Throwable) : ClientConnection(token), Failure
}

sealed class ClientDisconnection(override val token: IMqttToken?) : TokenizedClientEvent {
    class Successful(token: IMqttToken?) : ClientDisconnection(token), Success
    class Failed(token: IMqttToken?, override val throwable: Throwable) : ClientDisconnection(token), Failure
}

sealed class ClientSubscription(val topic: String, override val token: IMqttToken?) : TokenizedClientEvent {
    class Successful(token: IMqttToken?, topic: String) : ClientSubscription(topic, token), Success
    class Failed(token: IMqttToken?, topic: String, override val throwable: Throwable) :
        ClientSubscription(topic, token), Failure
}

sealed class ClientUnsubscribe(val topic: String, override val token: IMqttToken?) : TokenizedClientEvent {
    class Successful(token: IMqttToken?, topic: String) : ClientUnsubscribe(topic, token), Success
    class Failed(token: IMqttToken?, topic: String, override val throwable: Throwable) :
        ClientUnsubscribe(topic, token), Failure
}

sealed class ClientMessagePublish(val message: Message, override val token: IMqttToken?) : TokenizedClientEvent {
    class Successful(token: IMqttToken?, message: Message) : ClientMessagePublish(message, token), Success
    class Failed(token: IMqttToken?, message: Message, override val throwable: Throwable) :
        ClientMessagePublish(message, token), Failure
}

sealed class ClientMessageReceive() : Event {
    class Successful(val message: Message) : ClientMessageReceive(), Success
    class Failed(override val throwable: Throwable) :
        ClientMessageReceive(), Failure
}

class NotificationActivated(val notificationId: Int) : Event

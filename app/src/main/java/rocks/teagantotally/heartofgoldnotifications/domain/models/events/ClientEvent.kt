package rocks.teagantotally.heartofgoldnotifications.domain.models.events

import org.eclipse.paho.client.mqttv3.IMqttToken
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message

interface ClientEvent {
    val token: IMqttToken?
}

interface Success : ClientEvent

interface Failure : ClientEvent {
    val throwable: Throwable
}

interface ClientMessageEvent : ClientEvent

sealed class ClientConnection(override val token: IMqttToken?) : ClientEvent {
    class Successful(token: IMqttToken?) : ClientConnection(token), Success
    class Failed(token: IMqttToken?, override val throwable: Throwable) : ClientConnection(token), Failure
}

sealed class ClientDisconnection(override val token: IMqttToken?) : ClientEvent {
    class Successful(token: IMqttToken?) : ClientDisconnection(token), Success
    class Failed(token: IMqttToken?, override val throwable: Throwable) : ClientDisconnection(token), Failure
}

sealed class ClientSubscription(val topic: String, override val token: IMqttToken?) : ClientEvent {
    class Successful(token: IMqttToken?, topic: String) : ClientSubscription(topic, token), Success
    class Failed(token: IMqttToken?, topic: String, override val throwable: Throwable) :
        ClientSubscription(topic, token), Failure
}

sealed class ClientUnsubscription(val topic: String, override val token: IMqttToken?) : ClientEvent {
    class Successful(token: IMqttToken?, topic: String) : ClientUnsubscription(topic, token), Success
    class Failed(token: IMqttToken?, topic: String, override val throwable: Throwable) :
        ClientUnsubscription(topic, token), Failure
}

sealed class ClientMessagePublish(val message: Message, override val token: IMqttToken?) : ClientMessageEvent {
    class Successful(token: IMqttToken?, message: Message) : ClientMessagePublish(message, token), Success
    class Failed(token: IMqttToken?, message: Message, override val throwable: Throwable) :
        ClientMessagePublish(message, token), Failure
}

sealed class ClientMessageReceive(override val token: IMqttToken?) : ClientMessageEvent {
    class Successful(token: IMqttToken?, val message: Message) : ClientMessageReceive(token), Success
    class Failed(token: IMqttToken?, override val throwable: Throwable) :
        ClientMessageReceive(token), Failure
}

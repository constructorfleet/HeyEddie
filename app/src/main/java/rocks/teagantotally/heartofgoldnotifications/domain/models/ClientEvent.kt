package rocks.teagantotally.heartofgoldnotifications.domain.models

import org.eclipse.paho.client.mqttv3.IMqttToken

sealed class ClientEventType {
    object Connection : ClientEventType()
    object Subscribe : ClientEventType()
    object Unsubscribe : ClientEventType()
}

sealed class ClientEvent(val type: ClientEventType, val token: IMqttToken?) {
    class Success(type: ClientEventType, token: IMqttToken?) : ClientEvent(type, token)
    class Failed(type: ClientEventType, token: IMqttToken?, val throwable: Throwable) : ClientEvent(type, token)
}
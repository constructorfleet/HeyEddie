package rocks.teagantotally.heartofgoldnotifications.domain.models

import org.eclipse.paho.client.mqttv3.IMqttToken

sealed class MessageEvent {
    class Publish(val message: ReceivedMessage) : MessageEvent()

    sealed class PublishResult(val message: ReceivedMessage) : MessageEvent() {
        class Success(val token: IMqttToken?, message: ReceivedMessage) : PublishResult(message)

        class Failure(val throwable: Throwable, message: ReceivedMessage) : PublishResult(message)
    }

    sealed class Received : MessageEvent() {
        class Success(val message: ReceivedMessage) : Received()

        class Failure(val throwable: Throwable) : Received()
    }
}
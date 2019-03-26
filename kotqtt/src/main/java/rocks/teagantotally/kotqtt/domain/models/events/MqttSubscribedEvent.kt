package rocks.teagantotally.kotqtt.domain.models.events

data class MqttSubscribedEvent(val topic: String) : MqttEvent
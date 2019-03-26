package rocks.teagantotally.kotqtt.domain.models.events

data class MqttUnsubscribedEvent(val topic: String) : MqttEvent
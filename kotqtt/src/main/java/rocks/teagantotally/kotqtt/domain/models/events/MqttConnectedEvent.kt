package rocks.teagantotally.kotqtt.domain.models.events

data class MqttConnectedEvent(val reconnect: Boolean) : MqttEvent
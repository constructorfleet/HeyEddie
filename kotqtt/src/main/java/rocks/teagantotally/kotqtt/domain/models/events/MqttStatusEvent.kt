package rocks.teagantotally.kotqtt.domain.models.events

data class MqttStatusEvent(val isConnected: Boolean) : MqttEvent
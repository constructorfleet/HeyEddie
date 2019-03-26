package rocks.teagantotally.kotqtt.domain.models.events

data class MqttDisconnectedEvent(val connectionLost: Boolean = false) :
    MqttEvent
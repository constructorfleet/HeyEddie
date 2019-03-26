package rocks.teagantotally.kotqtt.domain.models.commands

data class MqttUnsubscribeCommand(val topic: String) : MqttCommand
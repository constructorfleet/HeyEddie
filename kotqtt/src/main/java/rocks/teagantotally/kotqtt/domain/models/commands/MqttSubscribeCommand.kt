package rocks.teagantotally.kotqtt.domain.models.commands

import rocks.teagantotally.kotqtt.domain.models.QoS

data class MqttSubscribeCommand(val topic: String, val qos: QoS) :
    MqttCommand
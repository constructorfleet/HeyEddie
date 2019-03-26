package rocks.teagantotally.kotqtt.domain.models.commands

import rocks.teagantotally.kotqtt.domain.models.Message

data class MqttPublishCommand(val message: Message) :
    MqttCommand
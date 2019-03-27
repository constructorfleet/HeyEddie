package rocks.teagantotally.kotqtt.domain.models.events

import rocks.teagantotally.kotqtt.domain.models.Message

data class MqttMessageReceived(val message: Message) :
    MqttEvent
package rocks.teagantotally.kotqtt.domain.models.events

import rocks.teagantotally.kotqtt.domain.models.Message

data class MqttMessagePublished(val message: Message) :
    MqttEvent
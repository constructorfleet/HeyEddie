package rocks.teagantotally.kotqtt.domain.models.events

import rocks.teagantotally.kotqtt.domain.models.Message
import java.util.*

data class MqttMessagePublished(val message: Message, val timestamp: Date) :
    MqttEvent
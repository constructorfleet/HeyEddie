package rocks.teagantotally.kotqtt.domain.framework.client

import kotlinx.coroutines.channels.ReceiveChannel
import rocks.teagantotally.kotqtt.domain.models.events.MqttEvent

interface MqttEventProducer {
    fun subscribe(): ReceiveChannel<MqttEvent>
}
package rocks.teagantotally.kotqtt.domain.models.commands

import rocks.teagantotally.kotqtt.domain.framework.connections.MqttBrokerConnection
import rocks.teagantotally.kotqtt.domain.models.events.MqttEvent

data class MqttConnectCommand(val brokerConnection: MqttBrokerConnection) :
    MqttCommand
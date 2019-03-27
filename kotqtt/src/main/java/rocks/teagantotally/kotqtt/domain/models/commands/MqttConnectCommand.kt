package rocks.teagantotally.kotqtt.domain.models.commands

import rocks.teagantotally.kotqtt.domain.framework.connections.MqttBrokerConnection

data class MqttConnectCommand(val brokerConnection: MqttBrokerConnection) :
    MqttCommand
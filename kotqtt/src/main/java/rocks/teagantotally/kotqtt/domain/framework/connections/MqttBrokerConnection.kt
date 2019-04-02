package rocks.teagantotally.kotqtt.domain.framework.connections

sealed class MqttBrokerConnection(val brokerUri: String, val clientId: String) {

    class TLSMqttBrokerConnection(brokerHost: String, brokerPort: Int, clientId: String) :
        MqttBrokerConnection("ssl://$brokerHost:$brokerPort", clientId)

    class InsecureMqttBrokerConnection(brokerHost: String, brokerPort: Int, clientId: String) :
        MqttBrokerConnection("tcp://$brokerHost:$brokerPort", clientId)

    // TODO: Websocket
}
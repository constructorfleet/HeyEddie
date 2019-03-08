package rocks.teagantotally.heartofgoldnotifications.domain.clients

import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import rocks.teagantotally.heartofgoldnotifications.domain.models.ConnectionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.ReceivedMessage

interface Client : MqttCallbackExtended {
    fun connect(connectionConfiguration: ConnectionConfiguration)

    fun disconnect()

    fun publish(message: ReceivedMessage)

    fun subscribe(topic: String, qosMax: Int)

    fun unsubscribe(topic: String)
}
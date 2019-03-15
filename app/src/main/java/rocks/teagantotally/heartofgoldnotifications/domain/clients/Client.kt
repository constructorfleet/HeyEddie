package rocks.teagantotally.heartofgoldnotifications.domain.clients

import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

interface Client : MqttCallbackExtended {
    sealed class ConnectionState(val message: String) {
        object Connected : ConnectionState("connected")
        object Disconnected : ConnectionState("disconnected")
        object Unknown : ConnectionState("Unknown")
        class Error(message: String) : ConnectionState(message)
    }

    interface ConnectionListener {
        fun onConnectionChange(state: ConnectionState)
    }

    fun addConnectionListener(listener: ConnectionListener)

    fun removeConnectionListener(listener: ConnectionListener)

    fun isConnected(): Boolean

    fun connect()

    fun disconnect()

    fun publish(message: Message)

    fun subscribe(topic: String, qosMax: Int)

    fun unsubscribe(topic: String)
}
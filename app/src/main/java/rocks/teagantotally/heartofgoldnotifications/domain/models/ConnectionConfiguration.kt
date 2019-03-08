package rocks.teagantotally.heartofgoldnotifications.domain.models

import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import rocks.teagantotally.heartofgoldnotifications.common.Transform
import java.io.Serializable
import java.net.InetAddress

data class ConnectionConfiguration(
    val brokerHost: String,
    val brokerPort: Int,
    val clientUsername: String,
    val clientPassword: String,
    val clientId: String,
    val autoReconnect: Boolean = false,
    val cleanSession: Boolean = false
// TODO : Last Will
) : Serializable, Transform<MqttConnectOptions> {
    override fun transform(): MqttConnectOptions =
        MqttConnectOptions()
            .apply {
                isAutomaticReconnect = autoReconnect
                isCleanSession = cleanSession
            }
}
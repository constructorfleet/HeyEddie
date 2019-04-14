package rocks.teagantotally.heartofgoldnotifications.domain.models.configs

import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import rocks.teagantotally.heartofgoldnotifications.common.Transform
import java.io.Serializable

data class ConnectionConfiguration(
    val brokerHost: String,
    val brokerPort: Int,
    val clientUsername: String? = null,
    val clientPassword: String? = null,
    val clientId: String,
    val autoReconnect: Boolean = false,
    val cleanSession: Boolean = false,
    val notificationCancelMinutes: Int = DEFAULT_AUTO_CANCEL_MINUTES
// TODO : Last Will
) : Serializable, Transform<MqttConnectOptions> {
    companion object {
        const val DEFAULT_AUTO_CANCEL_MINUTES = 60
    }

    override fun transform(): MqttConnectOptions =
        MqttConnectOptions()
            .apply {
                isAutomaticReconnect = autoReconnect
                isCleanSession = cleanSession
            }
}
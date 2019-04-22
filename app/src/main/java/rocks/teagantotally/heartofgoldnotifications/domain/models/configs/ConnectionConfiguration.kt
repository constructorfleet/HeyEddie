package rocks.teagantotally.heartofgoldnotifications.domain.models.configs

import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import rocks.teagantotally.heartofgoldnotifications.common.Transform
import rocks.teagantotally.kotqtt.domain.models.Message
import java.io.Serializable
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class CausesClientCreation

data class ConnectionConfiguration(
    @CausesClientCreation val brokerHost: String,
    @CausesClientCreation val brokerPort: Int,
    @CausesClientCreation val clientUsername: String? = null,
    @CausesClientCreation val clientPassword: String? = null,
    @CausesClientCreation val clientId: String,
    @CausesClientCreation val autoReconnect: Boolean = false,
    @CausesClientCreation val cleanSession: Boolean = false,
    val lastWill: Message? = null
// TODO : TLS
) : Serializable, Transform<MqttConnectOptions>, Configuration {
    companion object {
        private val RECREATE_CLIENT_FIELDS: Set<KProperty1<ConnectionConfiguration, *>> =
            ConnectionConfiguration::class.memberProperties
                .filter { it.findAnnotation<CausesClientCreation>() != null }
                .toSet()
    }

    override fun transform(): MqttConnectOptions =
        MqttConnectOptions()
            .apply {
                isAutomaticReconnect = autoReconnect
                isCleanSession = cleanSession
                lastWill?.let {
                    setWill(
                        it.topic,
                        it.payload,
                        it.qos.value,
                        it.retain
                    )
                }
            }

    fun shouldRecreateClient(oldConnectionConfiguration: ConnectionConfiguration): Boolean =
        RECREATE_CLIENT_FIELDS
            .any {
                it.get(oldConnectionConfiguration) != it.get(this)
            }
}
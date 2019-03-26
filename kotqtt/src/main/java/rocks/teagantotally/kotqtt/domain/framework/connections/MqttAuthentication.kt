package rocks.teagantotally.kotqtt.domain.framework.connections

sealed class MqttAuthentication {
    object Unauthenticated : MqttAuthentication()
    data class Basic(val username: String, val password: String) : MqttAuthentication()
}
package rocks.teagantotally.kotqtt.domain.framework.connections

import rocks.teagantotally.kotqtt.domain.framework.client.LastWill

data class MqttConnectionOptions(
    val authentication: MqttAuthentication,
    val keepAliveInterval: Int,
    val cleanSession: Boolean,
    val reconnect: Boolean,
    val useVersion31: Boolean = false,
    val lastWill: LastWill? = null
)
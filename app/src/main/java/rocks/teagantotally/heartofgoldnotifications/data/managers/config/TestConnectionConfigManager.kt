package rocks.teagantotally.heartofgoldnotifications.data.managers.config

import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration

class TestConnectionConfigManager :
    ConnectionConfigManager {
    val configuration =
        ConnectionConfiguration(
            brokerHost = "some.broker.com",
            brokerPort = 1883,
            cleanSession = true,
            clientId = "test",
            clientUsername = "username",
            clientPassword = "password",
            autoReconnect = true
        )

    override fun hasConnectionConfiguration(): Boolean = true

    override fun getConnectionConfiguration(): ConnectionConfiguration =
        configuration

    override fun setConnectionConfiguration(connectionConfiguration: ConnectionConfiguration) {
        // no-op
    }
}
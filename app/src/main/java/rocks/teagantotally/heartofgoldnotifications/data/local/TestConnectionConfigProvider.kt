package rocks.teagantotally.heartofgoldnotifications.data.local

import rocks.teagantotally.heartofgoldnotifications.domain.framework.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration

class TestConnectionConfigProvider :
    ConnectionConfigProvider {
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
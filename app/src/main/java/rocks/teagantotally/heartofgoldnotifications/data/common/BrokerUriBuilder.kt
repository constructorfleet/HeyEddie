package rocks.teagantotally.heartofgoldnotifications.data.common

import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration

object BrokerUriBuilder {
    private const val URI_FORMAT = "tcp://%s:%d"

    fun getBrokerUri(connectionConfiguration: ConnectionConfiguration): String =
        URI_FORMAT.format(
            connectionConfiguration.brokerHost,
            connectionConfiguration.brokerPort
        )
}
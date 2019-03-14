package rocks.teagantotally.heartofgoldnotifications.domain.framework

import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration

interface ConnectionConfigProvider {
    fun hasConnectionConfiguration(): Boolean

    fun getConnectionConfiguration(): ConnectionConfiguration

    fun setConnectionConfiguration(connectionConfiguration: ConnectionConfiguration)
}
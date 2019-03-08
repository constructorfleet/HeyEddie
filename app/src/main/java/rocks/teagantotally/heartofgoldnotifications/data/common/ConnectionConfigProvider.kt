package rocks.teagantotally.heartofgoldnotifications.data.common

import rocks.teagantotally.heartofgoldnotifications.domain.models.ConnectionConfiguration

interface ConnectionConfigProvider {
    fun hasConnectionConfiguration(): Boolean

    fun getConnectionConfiguration(): ConnectionConfiguration

    fun setConnectionConfiguration(connectionConfiguration: ConnectionConfiguration)
}
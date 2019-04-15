package rocks.teagantotally.heartofgoldnotifications.domain.framework.managers

import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration

interface ConnectionConfigManager {
    fun hasConnectionConfiguration(): Boolean

    fun getConnectionConfiguration(): ConnectionConfiguration?

    suspend fun setConnectionConfiguration(connectionConfiguration: ConnectionConfiguration)
}
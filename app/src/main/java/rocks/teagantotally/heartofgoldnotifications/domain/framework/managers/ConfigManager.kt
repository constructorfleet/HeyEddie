package rocks.teagantotally.heartofgoldnotifications.domain.framework.managers

import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.Configuration

interface ConfigManager<ConfigurationType: Configuration> {
    fun hasConfiguration(): Boolean

    fun getConfiguration(): ConfigurationType?

    suspend fun saveConfiguration(configuration: ConfigurationType)
}
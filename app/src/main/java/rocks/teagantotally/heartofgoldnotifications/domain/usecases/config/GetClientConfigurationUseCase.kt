package rocks.teagantotally.heartofgoldnotifications.domain.usecases.config

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCasesWithReturn
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import javax.inject.Inject

class GetClientConfigurationUseCase @Inject constructor(
    private val clientConfigManager: ConnectionConfigManager
) : UseCasesWithReturn<ConnectionConfiguration?> {
    override suspend fun invoke(): ConnectionConfiguration? =
        clientConfigManager.getConfiguration()
}
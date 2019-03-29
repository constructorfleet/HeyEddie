package rocks.teagantotally.heartofgoldnotifications.domain.usecases.connection

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.kotqtt.domain.framework.client.MqttCommandExecutor
import rocks.teagantotally.kotqtt.domain.models.commands.MqttDisconnectCommand
import rocks.teagantotally.kotqtt.domain.models.commands.MqttGetStatusCommand
import javax.inject.Inject

class GetClientStatus @Inject constructor(
    private val commandExecutor: MqttCommandExecutor
) : UseCase {
    override suspend fun invoke() {
        commandExecutor.execute(MqttGetStatusCommand)
    }
}
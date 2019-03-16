package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.commands.MqttCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand

class StopClientUseCase(
    private val commandExecutor: MqttCommandExecutor
) : UseCase<MqttCommand.Disconnect> {
    override suspend fun invoke(parameter: MqttCommand.Disconnect) {
        commandExecutor.execute(parameter)
    }
}
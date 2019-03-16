package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.MqttCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand

class StartClientUseCase(
    private val commandExecutor: MqttCommandExecutor
) : UseCase<MqttCommand.Connect> {

    override suspend fun invoke(parameter: MqttCommand.Connect) {
        commandExecutor.execute(parameter)
    }
}
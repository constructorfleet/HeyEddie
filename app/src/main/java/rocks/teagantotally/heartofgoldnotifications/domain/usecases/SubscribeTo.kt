package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.commands.MqttCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand

class SubscribeTo(
    private val commandExecutor: MqttCommandExecutor
) : UseCase<MqttCommand.Subscribe> {
    override suspend fun invoke(parameter: MqttCommand.Subscribe) {
        commandExecutor.execute(parameter)
    }
}
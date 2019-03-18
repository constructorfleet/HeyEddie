package rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.publish

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.commands.MqttCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand

class PublishMessage(
    private val commandExecutor: MqttCommandExecutor
) : UseCase<MqttCommand.Publish> {
    override suspend fun invoke(parameter: MqttCommand.Publish) {
        commandExecutor.execute(parameter)
    }
}
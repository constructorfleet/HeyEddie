package rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.message.publish

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.kotqtt.domain.framework.client.MqttCommandExecutor
import rocks.teagantotally.kotqtt.domain.models.commands.MqttPublishCommand
import javax.inject.Inject

class PublishMessage @Inject constructor(
    private val commandExecutor: MqttCommandExecutor
) : UseCaseWithParameter<MqttPublishCommand> {
    override suspend fun invoke(parameter: MqttPublishCommand) {
        commandExecutor.execute(parameter)
    }
}
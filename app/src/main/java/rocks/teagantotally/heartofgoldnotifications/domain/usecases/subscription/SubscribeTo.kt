package rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.kotqtt.domain.framework.client.MqttCommandExecutor
import rocks.teagantotally.kotqtt.domain.models.commands.MqttSubscribeCommand
import javax.inject.Inject

class SubscribeTo @Inject constructor(
    private val commandExecutor: MqttCommandExecutor
) : UseCaseWithParameter<MqttSubscribeCommand> {
    override suspend fun invoke(parameter: MqttSubscribeCommand) {
        commandExecutor.execute(parameter)
    }
}
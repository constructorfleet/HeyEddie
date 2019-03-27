package rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.kotqtt.domain.framework.client.MqttCommandExecutor
import rocks.teagantotally.kotqtt.domain.models.commands.MqttUnsubscribeCommand
import javax.inject.Inject

class UnsubscribeFrom @Inject constructor(
    private val commandExecutor: MqttCommandExecutor
) : UseCaseWithParameter<MqttUnsubscribeCommand> {
    override suspend fun invoke(parameter: MqttUnsubscribeCommand) {
        commandExecutor.execute(parameter)
    }
}
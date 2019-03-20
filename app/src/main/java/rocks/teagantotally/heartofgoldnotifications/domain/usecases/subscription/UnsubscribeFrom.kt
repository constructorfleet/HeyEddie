package rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.commands.MqttCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand

class UnsubscribeFrom(
    private val commandExecutor: MqttCommandExecutor
) : UseCase<MqttCommand.Unsubscribe> {
    override suspend fun invoke(parameter: MqttCommand.Unsubscribe) {
        commandExecutor.execute(parameter)
    }
}
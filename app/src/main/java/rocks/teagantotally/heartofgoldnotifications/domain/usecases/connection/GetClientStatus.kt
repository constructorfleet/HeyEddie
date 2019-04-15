package rocks.teagantotally.heartofgoldnotifications.domain.usecases.connection

import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.kotqtt.domain.framework.client.MqttCommandExecutor
import rocks.teagantotally.kotqtt.domain.models.commands.MqttGetStatusCommand
import javax.inject.Inject

class GetClientStatus @Inject constructor() : UseCase {

    @Inject
    lateinit var commandExecutor: MqttCommandExecutor

    override suspend fun invoke() {
        HeyEddieApplication
            .clientComponent
            ?.inject(this)
            ?.run { commandExecutor.execute(MqttGetStatusCommand) }
    }
}
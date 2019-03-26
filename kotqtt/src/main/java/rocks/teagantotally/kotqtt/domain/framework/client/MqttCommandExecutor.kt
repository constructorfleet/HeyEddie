package rocks.teagantotally.kotqtt.domain.framework.client

import rocks.teagantotally.kotqtt.domain.models.commands.MqttCommand

interface MqttCommandExecutor {
    suspend fun execute(command: MqttCommand)
}
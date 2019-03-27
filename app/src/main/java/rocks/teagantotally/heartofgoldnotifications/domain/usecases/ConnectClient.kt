package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import rocks.teagantotally.kotqtt.domain.framework.client.MqttCommandExecutor
import rocks.teagantotally.kotqtt.domain.framework.connections.MqttBrokerConnection
import rocks.teagantotally.kotqtt.domain.models.commands.MqttConnectCommand
import javax.inject.Inject

class ConnectClient @Inject constructor(
    private val config: ConnectionConfiguration,
    private val commandExecutor: MqttCommandExecutor
) : UseCase {

    override suspend fun invoke() {
        commandExecutor.execute(
            MqttConnectCommand(
                MqttBrokerConnection.InsecureMqttBrokerConnection(
                    config.brokerHost,
                    config.brokerPort,
                    config.clientId
                )
            )
        )
    }
}
package rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.disconnected

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.mqtt.MqttDisconnectedUseCase
import rocks.teagantotally.kotqtt.domain.models.events.MqttDisconnectedEvent

class MqttDisconnectedProcessor(
    private vararg val disconnectedProcessors: MqttDisconnectedUseCase
) : UseCaseWithParameter<MqttDisconnectedEvent> {
    override suspend fun invoke(parameter: MqttDisconnectedEvent) {
        when (parameter.connectionLost) {
            true -> MqttDisconnectedUseCase.Disconnect.ConnectionLost
            false -> MqttDisconnectedUseCase.Disconnect.Requested
        }.let { disconnect ->
            disconnectedProcessors
                .forEach { it.invoke(disconnect) }
        }
    }
}
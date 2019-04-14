package rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.connected

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.mqtt.MqttConnectedUseCase
import rocks.teagantotally.kotqtt.domain.models.events.MqttConnectedEvent

class MqttConnectedProcessor(
    private vararg val connectedProcessors: MqttConnectedUseCase
) : UseCaseWithParameter<MqttConnectedEvent> {
    override suspend fun invoke(parameter: MqttConnectedEvent) {
        connectedProcessors
            .forEach {
                it.invoke(
                    when (parameter.reconnect) {
                        true -> Connection.Reconnect
                        false -> Connection.New
                    }
                )
            }
    }
}
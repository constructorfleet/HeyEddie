package rocks.teagantotally.heartofgoldnotifications.domain.framework.event.mqtt

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter

interface MqttDisconnectedUseCase : UseCaseWithParameter<MqttDisconnectedUseCase.Disconnect> {
    sealed class Disconnect {
        object ConnectionLost: Disconnect()
        object Requested: Disconnect()
    }
}
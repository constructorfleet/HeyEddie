package rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.connected

import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.mqtt.MqttConnectedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.ClientState
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.UpdatePersistentNotificationUseCase

class SetConnectedPersistentNotificationUseCase(
    private val updatePersistentNotificationUseCase: UpdatePersistentNotificationUseCase
) : MqttConnectedUseCase {
    override suspend fun invoke(parameter: Connection) {
        updatePersistentNotificationUseCase(ClientState.Connected)
    }
}
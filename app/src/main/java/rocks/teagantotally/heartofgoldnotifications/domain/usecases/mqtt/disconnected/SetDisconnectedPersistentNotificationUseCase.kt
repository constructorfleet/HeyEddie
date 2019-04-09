package rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.disconnected

import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.mqtt.MqttDisconnectedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.ClientState
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.UpdatePersistentNotificationUseCase

class SetDisconnectedPersistentNotificationUseCase(
    private val updatePersistentNotificationUseCase: UpdatePersistentNotificationUseCase
) : MqttDisconnectedUseCase {
    override suspend fun invoke(parameter: MqttDisconnectedUseCase.Disconnect) {
        updatePersistentNotificationUseCase(ClientState.Disconnected)
    }
}
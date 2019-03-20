package rocks.teagantotally.heartofgoldnotifications.domain.usecases.device

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.device.DeviceConnectivityEventConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.DeviceEvent

class DeviceConnectivityAvailable(
    private val deviceConnectivityEventConsumer: DeviceConnectivityEventConsumer
) : UseCase<DeviceEvent> {
    override suspend fun invoke(parameter: DeviceEvent) {
        (parameter as? DeviceEvent.NetworkState.Available)
            ?.let { deviceConnectivityEventConsumer.consume(it) }
    }
}
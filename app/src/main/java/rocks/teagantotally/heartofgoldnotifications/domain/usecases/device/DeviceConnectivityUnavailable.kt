package rocks.teagantotally.heartofgoldnotifications.domain.usecases.device

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.device.DeviceConnectivityEventConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.DeviceEvent

class DeviceConnectivityUnavailable(
    private val deviceConnectivityEventConsumer: DeviceConnectivityEventConsumer
) : UseCaseWithParameter<DeviceEvent> {
    override suspend fun invoke(parameter: DeviceEvent) {
        (parameter as? DeviceEvent.NetworkState.Unavailable)
            ?.let { deviceConnectivityEventConsumer.consume(it) }
    }
}
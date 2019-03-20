package rocks.teagantotally.heartofgoldnotifications.domain.usecases.device

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.DeviceEvent

class ProcessDeviceEvent(
    private vararg val eventProcessors: UseCase<DeviceEvent>
) : UseCase<DeviceEvent> {
    override suspend fun invoke(parameter: DeviceEvent) {
        eventProcessors
            .forEach { it(parameter) }
    }
}
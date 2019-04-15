package rocks.teagantotally.heartofgoldnotifications.domain.usecases.device

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.DeviceEvent

//class DeviceConnectivityAvailable(
//    private val deviceConnectivityEventConsumer: DeviceConnectivityEventConsumer
//) : UseCaseWithParameter<DeviceEvent> {
//    override suspend fun invoke(parameter: DeviceEvent) {
//        (parameter as? DeviceEvent.NetworkState.Available)
//            ?.let { deviceConnectivityEventConsumer.consume(it) }
//    }
//}
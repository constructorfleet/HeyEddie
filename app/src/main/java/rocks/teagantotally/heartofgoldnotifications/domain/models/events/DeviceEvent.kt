package rocks.teagantotally.heartofgoldnotifications.domain.models.events

sealed class DeviceEvent : Event {
    sealed class NetworkState() : DeviceEvent() {
        object Unavailable : NetworkState()
        object Available : NetworkState()
    }
}
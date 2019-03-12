package rocks.teagantotally.heartofgoldnotifications.domain.models.events

sealed class ConnectionEvent : Event {
    object Connected : ConnectionEvent()
    object Disconnected : ConnectionEvent()
}
package rocks.teagantotally.heartofgoldnotifications.domain.models

sealed class ConnectEvent {
    object Connect : ConnectEvent()

    object Disconnect : ConnectEvent()
}
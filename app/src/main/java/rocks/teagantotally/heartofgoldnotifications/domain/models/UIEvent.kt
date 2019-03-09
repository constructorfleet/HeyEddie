package rocks.teagantotally.heartofgoldnotifications.domain.models

sealed class UIEvent {
    object Connected : UIEvent()
    object Disconnected: UIEvent()
    class Received(val message: String): UIEvent()
}
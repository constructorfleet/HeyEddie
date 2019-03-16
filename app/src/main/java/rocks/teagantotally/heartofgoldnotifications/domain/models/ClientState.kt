package rocks.teagantotally.heartofgoldnotifications.domain.models

sealed class ClientState(val message: String) {
    object Connected : ClientState("Connected to broker")
    object Disconnected: ClientState("Disconnected")
    object Unknown: ClientState("Not initialized")
}
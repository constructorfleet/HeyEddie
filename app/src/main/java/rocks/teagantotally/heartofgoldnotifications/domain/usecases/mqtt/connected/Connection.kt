package rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.connected

sealed class Connection {
    object Reconnect : Connection()
    object New : Connection()
}
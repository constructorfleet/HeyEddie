package rocks.teagantotally.heartofgoldnotifications.domain.models.events

sealed class ConnectionEvent(val isConnected: Boolean) : Event {
    object Connected : ConnectionEvent(true)
    object Disconnected : ConnectionEvent(false)
//    class Status(isConnected: Boolean) : ConnectionEvent(isConnected)
//    class Failed(override val source: ConnectionCommand, override val throwable: Throwable?) :
//        ConnectionEvent(source !is ConnectionCommand.Connect),
//        Failure<ConnectionCommand>
}
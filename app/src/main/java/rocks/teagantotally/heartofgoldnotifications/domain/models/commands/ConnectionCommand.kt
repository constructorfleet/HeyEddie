package rocks.teagantotally.heartofgoldnotifications.domain.models.commands

sealed class ConnectionCommand : Command {
    object Connect : ConnectionCommand()
    object Disconnect : ConnectionCommand()
    object GetStatus : ConnectionCommand()
}
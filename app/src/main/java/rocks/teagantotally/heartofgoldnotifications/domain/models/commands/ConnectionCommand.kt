package rocks.teagantotally.heartofgoldnotifications.domain.models.commands

import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Event

sealed class ConnectionCommand : Event {
    object Connect : ConnectionCommand()
    object Disconnect : ConnectionCommand()
    object Status : ConnectionCommand()
}
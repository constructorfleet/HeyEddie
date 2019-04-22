package rocks.teagantotally.heartofgoldnotifications.domain.models.commands

import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage

sealed class NotificationCommand : Command {
    class Notify(val notificationMessage: NotificationMessage) : NotificationCommand()
    class Dismiss(val notificationId: Int, val autoDismiss: Boolean) : NotificationCommand()
}
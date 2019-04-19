package rocks.teagantotally.heartofgoldnotifications.domain.framework

import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessageChannel

interface Notifier {
    fun createChannel(channel: NotificationMessageChannel)

    fun notify(notification: NotificationMessage, alertAlways: Boolean = true)

    fun dismiss(notificationId: Int)
}
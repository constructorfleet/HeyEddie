package rocks.teagantotally.heartofgoldnotifications.data.common

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager

interface Notifier {
    fun createNotificationChannel(channel: NotificationChannel)

    fun notify(notificationId: Int, notification: Notification)
}

class NotifierService(
    private val notificationManager: NotificationManager
) : Notifier {
    override fun createNotificationChannel(channel: NotificationChannel) =
        notificationManager.createNotificationChannel(channel)

    override fun notify(notificationId: Int, notification: Notification) =
        notificationManager.notify(notificationId, notification)
}
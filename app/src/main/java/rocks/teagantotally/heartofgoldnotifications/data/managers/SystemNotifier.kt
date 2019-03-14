package rocks.teagantotally.heartofgoldnotifications.data.managers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifAlso
import rocks.teagantotally.heartofgoldnotifications.common.extensions.unique
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessageChannel
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import kotlin.coroutines.CoroutineContext

class SystemNotifier(
    private val context: Context,
    private val notificationManager: NotificationManager
) : Notifier, Scoped {

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext by lazy { job.plus(Dispatchers.Main) }

    override fun notify(notification: NotificationMessage) {
        createChannel(notification.channel)
        notification.transform(context)
            .let {
                notificationManager.notify(it.first, it.second)
            }
    }

    override fun dismiss(notificationId: Int) =
        notificationManager.cancel(notificationId)

    private fun createChannel(notificationChannel: NotificationMessageChannel) {
        notificationManager.createNotificationChannel(notificationChannel.transform())
    }

    private fun NotificationMessageChannel.transform(): NotificationChannel =
        NotificationChannel(id, name, importance.systemValue)
            .also { channel ->

                channel.enableLights(enableLights)
                if (enableLights) {
                    channel.lightColor = lightColor
                }
                vibrationPattern?.let {
                    channel.vibrationPattern = vibrationPattern
                }
                channel.lockscreenVisibility = visibility.systemValue
                channel.description = description
                channel.name = name
                channel.importance = importance.systemValue
            }
}

fun NotificationMessage.transform(context: Context): Pair<Int, Notification> =
    Pair(
        notificationId,
        Notification.Builder(context, channel.id)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(autoCancel)
            .setOngoing(onGoing)
            .extend(Notification.WearableExtender())
            .extend(Notification.CarExtender())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setWhen(System.currentTimeMillis())
            .ifAlso({ !actions.isNullOrEmpty() }) { builder ->
                actions
                    .forEach { action ->
                        Intent(context, MqttService.PublishReceiver::class.java)
                            .apply {
                                putExtra(
                                    MqttService.PublishReceiver.KEY_NOTIFICATION_ID,
                                    notificationId
                                )
                                putExtra(
                                    MqttService.PublishReceiver.KEY_MESSAGE,
                                    Message(
                                        action.topic,
                                        action.payload,
                                        action.qos,
                                        action.retain
                                    ) as Parcelable
                                )
                            }
                            .let {
                                PendingIntent.getBroadcast(context, Int.unique(), it, PendingIntent.FLAG_UPDATE_CURRENT)
                            }
                            .let {
                                Notification.Action.Builder(0, action.text, it)
                                    .build()
                            }
                            .let { builder.addAction(it) }
                    }
            }
            .build()
            .also {
                if (onGoing) {
                    it.flags += Notification.FLAG_ONGOING_EVENT
                } else {
                    it.flags += Notification.FLAG_AUTO_CANCEL
                }
            }
    )
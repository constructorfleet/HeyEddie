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
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessageChannel
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationVisibility
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
        NotificationChannel(id, name, importance)
            .also { channel ->

                channel.enableLights(enableLights)
                if (enableLights) {
                    channel.lightColor = lightColor
                }
                vibrationPattern?.let {
                    channel.vibrationPattern = vibrationPattern
                }
                visibility
                    .let {
                        if (it.isNullOrEmpty()) {
                            NotificationVisibility.PRIVATE.systemValue
                        } else {
                            NotificationVisibility.valueOf(it).systemValue
                        }
                    }
                    .let { channel.lockscreenVisibility = it }
                channel.description = description
                channel.name = name
                channel.importance = importance
            }
}

fun NotificationMessage.transform(context: Context): Pair<Int, Notification> =
    Pair(
        id,
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
                    .forEach {
                        val intent = Intent(context, MqttService.PublishReceiver::class.java)
                            .apply {
                                putExtra(
                                    MqttService.PublishReceiver.KEY_NOTIFICATION_ID,
                                    id
                                )
                                putExtra(
                                    MqttService.PublishReceiver.KEY_MESSAGE,
                                    Message(
                                        it.topic,
                                        it.payload,
                                        it.qos,
                                        it.retain
                                    ) as Parcelable
                                )
                            }
                        val pendingIntent =
                            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        Notification.Action.Builder(0, it.text, pendingIntent)
                            .build()
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
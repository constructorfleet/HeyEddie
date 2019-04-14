package rocks.teagantotally.heartofgoldnotifications.data.managers

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifAlso
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrue
import rocks.teagantotally.heartofgoldnotifications.common.extensions.putInvoker
import rocks.teagantotally.heartofgoldnotifications.common.extensions.unique
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_DISMISS
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_PUBLISH
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_MESSAGE
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_NOTIFICATION_ID
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessageChannel
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivity
import rocks.teagantotally.kotqtt.domain.models.Message
import rocks.teagantotally.kotqtt.domain.models.QoS
import java.util.*

class SystemNotifier(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val connectionConfigManager: ConnectionConfigManager,
    private val alarmManager: AlarmManager
) : Notifier {
    override fun notify(notification: NotificationMessage) {
        createChannel(notification.channel)
        notification.transform(context)
            .also {
                notificationManager.notify(it.first, it.second)
            }
            .ifTrue({ !notification.onGoing }) {
                Intent(ACTION_DISMISS)
                    .putExtra(EXTRA_NOTIFICATION_ID, it.first)
                    .let {
                        PendingIntent.getBroadcast(
                            context,
                            Int.unique(),
                            it,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }
                    .let {
                        connectionConfigManager.getConnectionConfiguration()
                            .let { it?.notificationCancelMinutes ?: ConnectionConfiguration.DEFAULT_AUTO_CANCEL_MINUTES }
                            .let { it * 60 * 1000 }
                            .let { cancelDelay ->
                                alarmManager.set(
                                    AlarmManager.RTC,
                                    System.currentTimeMillis() + cancelDelay,
                                    it
                                )
                            }
                    }
            }
    }

    override fun dismiss(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    override fun createChannel(notificationChannel: NotificationMessageChannel) {
        try {
            notificationManager.getNotificationChannel(notificationChannel.id)
        } catch (_: Throwable) {
            null
        } ?: notificationManager.createNotificationChannel(notificationChannel.transform())
    }

    private fun NotificationMessageChannel.transform(): NotificationChannel =
        NotificationChannel(id, name, (importance ?: NotificationMessageChannel.DEFAULT_IMPORTANCE).systemValue)
            .also { channel ->

                channel.enableLights(enableLights)
                if (enableLights) {
                    channel.lightColor = lightColor
                }
                vibrationPattern?.let {
                    channel.vibrationPattern = vibrationPattern
                }
                channel.lockscreenVisibility = (visibility ?: NotificationMessageChannel.DEFAULT_VISIBILITY).systemValue
                channel.description = description
                channel.name = name
                channel.setSound(null, null)
            }
}

@UseExperimental(ExperimentalCoroutinesApi::class)
@ObsoleteCoroutinesApi
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
            .ifAlso({ openApplication }) { builder ->
                Intent(context, MainActivity::class.java)
                    .let {
                        it.putInvoker(SystemNotifier::class)
                        PendingIntent.getActivity(
                            context,
                            Int.unique(),
                            it,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }
                    .let { builder.setContentIntent(it) }
            }
            .ifAlso({ !actions.isNullOrEmpty() }) { builder ->
                actions
                    .forEach { action ->
                        Intent(ACTION_PUBLISH)
                            .putInvoker(SystemNotifier::class)
                            .putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                            .putExtra(
                                EXTRA_MESSAGE,
                                Message(
                                    action.topic,
                                    action.retain,
                                    QoS.fromQoS(action.qos),
                                    action.payload.toByteArray(),
                                    Date()
                                ) as Parcelable
                            )
                            .let {
                                PendingIntent.getBroadcast(
                                    context,
                                    Int.unique(),
                                    it,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                                )
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
                it.flags += if (onGoing) Notification.FLAG_ONGOING_EVENT else Notification.FLAG_AUTO_CANCEL
            }
    )
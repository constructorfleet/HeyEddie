package rocks.teagantotally.heartofgoldnotifications.data.managers

import android.app.*
import android.app.Notification.EXTRA_TEXT
import android.app.Notification.EXTRA_TITLE
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Parcelable
import com.bumptech.glide.Glide
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.common.extensions.*
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_DISMISS
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_PUBLISH
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_MESSAGE
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_NOTIFICATION_AUTO_DISMISSED
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_NOTIFICATION_ID
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.NotificationConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.NotificationConfiguration.Companion.DEFAULT_AUTO_CANCEL_MINUTES
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessageChannel
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivity
import rocks.teagantotally.kotqtt.domain.models.Message
import rocks.teagantotally.kotqtt.domain.models.QoS
import java.util.*

class SystemNotifier(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val notificationConfigManager: NotificationConfigManager,
    private val alarmManager: AlarmManager
) : Notifier, SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private val notificationGroupMap: MutableMap<String, NotificationGroup> = mutableMapOf()
        private val notificationMap: MutableMap<Int, Notification> = mutableMapOf()
        private val debugNotificationGroupId: Int = Int.unique()
        const val debugNotificationGroupName: String = "Debug"
        lateinit var debugNotificationGroup: Notification
        lateinit var debugNotificationChannel: NotificationMessageChannel
        private var debugNotificationIds: MutableList<Int> = mutableListOf()
    }

    init {
        debugNotificationChannel =
            NotificationMessageChannel(
                debugNotificationGroupName,
                debugNotificationGroupName,
                "Debug messages",
                false
            )
        debugNotificationGroup = createGroupSummary(debugNotificationChannel).first
        notificationConfigManager.getConfiguration()
            ?.debug
            .let { debugMode(it ?: false) }
        notificationConfigManager.addOnConfigurationChangeListener(this)
    }

    override fun notify(notification: NotificationMessage, alertAlways: Boolean) {
        createChannel(notification.channel)
        notification.transform(context, alertAlways)
            .also {
                val notificationId = it.first
                notificationMap.put(notificationId, it.second)
                notificationManager.notify(notificationId, it.second)
                createGroupSummary(notification.channel, notificationGroupMap.get(notification.channel.name))
                    .let {
                        it.second.notificationIds.add(notificationId)
                        notificationManager.notify(it.second.notificationId, it.first)
                    }
            }
            .ifTrue({ !notification.onGoing }) {
                Intent(ACTION_DISMISS)
                    .putExtra(EXTRA_NOTIFICATION_ID, it.first)
                    .also {
                        PendingIntent.getBroadcast(
                            context,
                            Int.unique(),
                            it,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }
                    .let {
                        Intent(it)
                            .putExtra(EXTRA_NOTIFICATION_AUTO_DISMISSED, true)
                    }
                    .let {
                        PendingIntent.getBroadcast(
                            context,
                            Int.unique(),
                            it,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }
                    .let {
                        notificationConfigManager.getConfiguration()
                            .ifMaybe({ it?.autoCancel ?: true }) { it }
                            ?.let {
                                it.notificationCancelMinutes
                            }
                            ?.let { it * 60 * 1000 }
                            ?.let { cancelDelay ->
                                alarmManager.set(
                                    AlarmManager.RTC,
                                    System.currentTimeMillis() + cancelDelay,
                                    it
                                )
                            }
                    }
            }
    }

    override fun dismiss(notificationId: Int, autoDismiss: Boolean) {
        notificationManager.cancel(notificationId)
        notificationGroupMap
            .values
            .firstOrNull {
                it.notificationIds.contains(notificationId)
            }
            ?.let { it.decrementGroup() }
            ?.ifTrue({ it.count == 0 }) {
                notificationManager.cancel(it.notificationId)
                notificationGroupMap.remove(it.groupId)
            }
        notificationConfigManager.getConfiguration()
            ?.ifTrue({ it.debug && autoDismiss }) {
                notificationMap[notificationId]
                    ?.let { notification ->
                        Int.unique()
                            .let { debugId ->
                                notificationManager.notify(
                                    debugId,
                                    Notification.Builder(context, debugNotificationGroupName)
                                        .setGroup(debugNotificationGroupName)
                                        .setContentTitle("Dismissed ${notification.extras.getCharSequence(EXTRA_TITLE)}")
                                        .setContentText("Notification ${notification.extras.getCharSequence(EXTRA_TEXT)}")
                                        .setAutoCancel(false)
                                        .setOngoing(false)
                                        .setWhen(System.currentTimeMillis())
                                        .setShowWhen(true)
                                        .setSmallIcon(R.drawable.ic_hitchhiker_symbol)
                                        .build()
                                        .also { debugNotificationIds.add(debugId) }
                                )
                            }
                        notificationMap.remove(notificationId)
                    }
            }
        notificationMap.remove(notificationId)
    }

    override fun createChannel(notificationChannel: NotificationMessageChannel) {
        try {
            notificationManager.getNotificationChannel(notificationChannel.id)
        } catch (_: Throwable) {
            null
        } ?: notificationManager.createNotificationChannel(notificationChannel.transform())
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        sharedPreferences?.getBoolean(context.getString(R.string.pref_notification_debug), false)
            .let {
                debugMode(it ?: false)
            }
    }

    private fun createGroupSummary(
        notificationChannel: NotificationMessageChannel,
        notificationGroup: NotificationGroup? = null
    ): Pair<Notification, NotificationGroup> =
        Pair(
            Notification.Builder(context, notificationChannel.id)
                .setContentTitle(notificationChannel.name)
                .setContentText(notificationChannel.description)
                .setGroup(notificationChannel.name)
                .setNumber(notificationGroup?.count ?: 1)
                .setGroupSummary(true)
                .setSmallIcon(R.drawable.ic_hitchhiker_symbol)
                .build(),
            notificationGroup?.incrementGroup()
                ?: NotificationGroup(
                    notificationChannel.name
                )
        ).also {
            notificationGroupMap[notificationChannel.name] = it.second
        }

    private fun debugMode(enabled: Boolean) {
        if (enabled) {
            notificationManager.notify(debugNotificationGroupId, debugNotificationGroup)
            debugNotificationIds.clear()
        } else {
            debugNotificationIds.forEach {
                notificationManager.cancel(it)
            }
            notificationManager.cancel(debugNotificationGroupId)
        }
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

    private data class NotificationGroup(
        val groupId: String,
        val summaryId: Int = Int.unique(),
        val notificationId: Int = Int.unique(),
        var count: Int = 1,
        val notificationIds: MutableSet<Int> = mutableSetOf()
    ) {
        fun incrementGroup(): NotificationGroup =
            this.also { count++ }

        fun decrementGroup(): NotificationGroup =
            this.also { count-- }
    }
}

@UseExperimental(ExperimentalCoroutinesApi::class)
@ObsoleteCoroutinesApi
fun NotificationMessage.transform(context: Context, alertAlways: Boolean): Pair<Int, Notification> =
    Pair(
        notificationId,
        Notification.Builder(context, channel.id)
            .setGroup(channel.name)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(autoCancel)
            .setOnlyAlertOnce(!alertAlways)
            .setOngoing(onGoing)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(true)
            .setSmallIcon(R.drawable.ic_hitchhiker_symbol)
            .extend(Notification.WearableExtender())
            .extend(Notification.CarExtender())
            .setDeleteIntent(
                Intent(ACTION_DISMISS)
                    .putInvoker(SystemNotifier::class)
                    .putExtra(EXTRA_NOTIFICATION_ID, notificationId)
                    .let {
                        PendingIntent.getBroadcast(
                            context,
                            Int.unique(),
                            it,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }
            )
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
            .ifAlso({ imageUrl?.isNotBlank() == true }) { builder ->
                val scaleMultiplier = context.resources.displayMetrics.density / 3.0
                Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get()
                    ?.let {
                        builder.style = Notification.BigPictureStyle()
                            .bigPicture(it)
                        Bitmap.createScaledBitmap(
                            it,
                            (it.width * scaleMultiplier).toInt(),
                            (it.height * scaleMultiplier).toInt()
                            , false
                        ).let { builder.setLargeIcon(it) }
                    }
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
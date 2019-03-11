package rocks.teagantotally.heartofgoldnotifications.domain.models

import android.app.Notification
import android.app.NotificationManager
import android.os.Parcelable
import android.support.annotation.ColorRes
import kotlinx.android.parcel.Parcelize
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Event
import java.io.Serializable

@Parcelize
class Message(
    val topic: String,
    val payload: String,
    val qos: Int,
    val retain: Boolean
) : Parcelable, Serializable, Event

enum class NotificationVisibility(val systemValue: Int) {
    PUBLIC(Notification.VISIBILITY_PUBLIC),
    PRIVATE(Notification.VISIBILITY_PRIVATE),
    SECRET(Notification.VISIBILITY_SECRET);
}

@Parcelize
class NotificationMessageChannel(
    val id: String = android.app.NotificationChannel.DEFAULT_CHANNEL_ID,
    val name: String,
    val description: String,
    val enableLights: Boolean = false,
    @ColorRes val lightColor: Int = 0,
    val visibility: String = NotificationVisibility.PRIVATE.name,
    val vibrationPattern: LongArray? = null,
    val importance: Int = NotificationManager.IMPORTANCE_LOW
) : Parcelable

@Parcelize
class NotificationMessageAction(
    val text: String,
    val topic: String,
    val payload: String,
    val qos: Int = 0,
    val retain: Boolean = false
) : Parcelable

@Parcelize
class NotificationMessage(
    val channel: NotificationMessageChannel,
    val id: Int,
    val title: String,
    val body: String,
    val onGoing: Boolean = false,
    val autoCancel: Boolean = true,
    val priority: Int = NotificationManager.IMPORTANCE_DEFAULT,
    val actions: List<NotificationMessageAction> = listOf()
) : Parcelable
package rocks.teagantotally.heartofgoldnotifications.domain.models.messages

import android.app.Notification
import android.app.NotificationManager
import android.os.Parcelable
import android.support.annotation.ColorRes
import kotlinx.android.parcel.Parcelize
import rocks.teagantotally.heartofgoldnotifications.common.extensions.unique

enum class NotificationVisibility(val systemValue: Int) {
    PUBLIC(Notification.VISIBILITY_PUBLIC),
    PRIVATE(Notification.VISIBILITY_PRIVATE),
    SECRET(Notification.VISIBILITY_SECRET);
}

enum class NotificationImportance(val systemValue: Int) {
    MIN(NotificationManager.IMPORTANCE_MIN),
    LOW(NotificationManager.IMPORTANCE_LOW),
    MID(NotificationManager.IMPORTANCE_DEFAULT),
    HIGH(NotificationManager.IMPORTANCE_HIGH),
    MAX(NotificationManager.IMPORTANCE_MAX)
}

@Parcelize
class NotificationMessageChannel(
    val id: String = android.app.NotificationChannel.DEFAULT_CHANNEL_ID,
    val name: String,
    val description: String,
    val enableLights: Boolean = false,
    @ColorRes val lightColor: Int = 0,
    val visibility: NotificationVisibility = NotificationVisibility.PRIVATE,
    val vibrationPattern: LongArray? = null,
    val importance: NotificationImportance = NotificationImportance.MID
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
    var id: Int? = null,
    val title: String,
    val body: String,
    val onGoing: Boolean = false,
    val autoCancel: Boolean = true,
    val importance: NotificationImportance = NotificationImportance.MID,
    val actions: List<NotificationMessageAction> = listOf()
) : Parcelable {
    val notificationId: Int
        get() = id ?: Int.unique().also { id = it }
}
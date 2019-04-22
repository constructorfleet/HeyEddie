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
    var visibility: NotificationVisibility? = null,
    val vibrationPattern: LongArray? = null,
    var importance: NotificationImportance? = null
) : Parcelable {
    companion object {
        val DEFAULT_IMPORTANCE = NotificationImportance.MID
        val DEFAULT_VISIBILITY = NotificationVisibility.PRIVATE
    }
}

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
    override var id: Int? = null,
    override var title: String,
    override var body: String,
    override var topic: String,
    val openApplication: Boolean = false,
    val onGoing: Boolean = false,
    val autoCancel: Boolean = true,
    val actions: List<NotificationMessageAction> = listOf()
) : Parcelable, ReceivedMessage {
    val notificationId: Int
        get() = id ?: Int.unique().also { id = it }
}
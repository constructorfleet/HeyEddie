package rocks.teagantotally.heartofgoldnotifications.domain.models

import android.app.NotificationManager
import java.io.Serializable

class ReceivedMessage(
    val topic: String,
    val payload: ByteArray,
    val qos: Int,
    val retain: Boolean
) : Serializable

class NotificationChannel(
    val id: String = android.app.NotificationChannel.DEFAULT_CHANNEL_ID,
    val name: String,
    val description: String,
    val importance: Int = NotificationManager.IMPORTANCE_LOW
) : Serializable

class NotificationAction(
    val text: String,
    val topic: String,
    val payload: Serializable
) : Serializable

class NotificationMessage(
    val channel: NotificationChannel,
    val title: String,
    val body: String,
    val priority: Int = NotificationManager.IMPORTANCE_DEFAULT,
    val actions: List<NotificationAction> = listOf()
) : Serializable
package rocks.teagantotally.heartofgoldnotifications.domain.models.messages

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
open class Message(
    val topic: String,
    val payload: String,
    val qos: Int,
    val retain: Boolean,
    val date: Date
) : Parcelable, Serializable

fun Message.onPublish(): Message =
    Message(
        topic,
        payload,
        qos,
        retain,
        Date()
    )
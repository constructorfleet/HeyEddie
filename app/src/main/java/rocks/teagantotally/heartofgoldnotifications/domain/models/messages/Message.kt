package rocks.teagantotally.heartofgoldnotifications.domain.models.messages

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import rocks.teagantotally.heartofgoldnotifications.common.extensions.empty
import java.io.Serializable

@Parcelize
open class Message(
    val topic: String,
    val payload: String,
    val qos: Int,
    val retain: Boolean
) : Parcelable, Serializable

object EmptyMessage : Message(String.empty(), String.empty(), 0, false)
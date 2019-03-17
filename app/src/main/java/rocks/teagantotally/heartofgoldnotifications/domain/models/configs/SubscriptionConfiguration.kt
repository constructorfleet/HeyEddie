package rocks.teagantotally.heartofgoldnotifications.domain.models.configs

import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType
import java.io.Serializable

open class SubscriptionConfiguration(
    val topic: String,
    val maxQoS: Int,
    val messageType: MessageType
) : Serializable {
    override fun equals(other: Any?): Boolean =
        (other as? SubscriptionConfiguration)
            ?.let { it.topic == topic }
            ?: false

    override fun hashCode(): Int =
        topic.hashCode()
}
package rocks.teagantotally.heartofgoldnotifications.domain.models.configs

import java.io.Serializable

data class SubscriptionConfiguration(
    val topic: String,
    val maxQoS: Int
) : Serializable
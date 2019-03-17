package rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions

import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType

sealed class SubscriptionViewModel {
    data class NewSubscription(
        var topic: String? = null,
        var maxQoS: Int? = null,
        var messageType: MessageType? = null
    ) : SubscriptionViewModel() {
        val isValid: Boolean
            get() = !topic.isNullOrBlank() && maxQoS != null && messageType != null
    }

    data class ActiveSubscription(
        val topic: String,
        val maxQoS: Int,
        val messageType: MessageType
    ) : SubscriptionViewModel()
}
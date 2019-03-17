package rocks.teagantotally.heartofgoldnotifications.domain.framework.managers

import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

interface MessageHistoryManager {
    interface Listener {
        fun onMessageReceived(message: Message)

        fun onMessagePublished(message: Message)
    }

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    fun recordMessageReceived(message: Message)

    fun recordMessagePublished(message: Message)

    fun getReceivedMessages(): List<Message>

    fun getPublishedMessages(): List<Message>

    fun clear()
}
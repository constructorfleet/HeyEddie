package rocks.teagantotally.heartofgoldnotifications.domain.framework

import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

interface MessageHistoryManager {
    fun recordMessageReceived(message: Message)

    fun recordMessagePublished(message: Message)

    fun getReceivedMessages(): List<Message>

    fun getPublishedMessages(): List<Message>
}
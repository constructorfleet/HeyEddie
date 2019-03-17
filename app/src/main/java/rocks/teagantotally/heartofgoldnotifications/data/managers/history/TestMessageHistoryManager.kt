package rocks.teagantotally.heartofgoldnotifications.data.managers.history

import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.MessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import java.util.*

class TestMessageHistoryManager : MessageHistoryManager {
    private val receivedMessages: MutableList<Message> = mutableListOf(
        Message(
            "topic",
            "Received",
            0,
            false,
            Date()
        )
    )
    private val publishedMessages: MutableList<Message> = mutableListOf(
        Message(
            "topic",
            "Received",
            0,
            false,
            Date()
        )
    )
    private val listeners: MutableList<MessageHistoryManager.Listener> = mutableListOf()

    override fun addListener(listener: MessageHistoryManager.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: MessageHistoryManager.Listener) {
        listeners.remove(listener)
    }

    override fun recordMessageReceived(message: Message) {
        receivedMessages.add(message)
        listeners
            .forEach { it.onMessageReceived(message) }
    }

    override fun recordMessagePublished(message: Message) {
        publishedMessages.add(message)
        listeners
            .forEach { it.onMessagePublished(message) }
    }

    override fun clear() {
        receivedMessages.clear()
        publishedMessages.clear()
    }

    override fun getReceivedMessages(): List<Message> = receivedMessages

    override fun getPublishedMessages(): List<Message> = publishedMessages
}
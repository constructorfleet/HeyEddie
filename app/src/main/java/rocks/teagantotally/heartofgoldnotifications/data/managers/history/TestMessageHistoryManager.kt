package rocks.teagantotally.heartofgoldnotifications.data.managers.history

import rocks.teagantotally.heartofgoldnotifications.domain.framework.MessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import java.util.*

class TestMessageHistoryManager : MessageHistoryManager {
    override fun addListener(listener: MessageHistoryManager.Listener) {

    }

    override fun removeListener(listener: MessageHistoryManager.Listener) {

    }

    override fun recordMessageReceived(message: Message) {

    }

    override fun recordMessagePublished(message: Message) {

    }

    override fun getReceivedMessages(): List<Message> =
        listOf(
            Message(
                "topic",
                "Received",
                0,
                false,
                Date()
            )
        )

    override fun getPublishedMessages(): List<Message> =
        listOf(
            Message(
                "topic",
                "Published",
                0,
                false,
                Date()
            )
        )
}
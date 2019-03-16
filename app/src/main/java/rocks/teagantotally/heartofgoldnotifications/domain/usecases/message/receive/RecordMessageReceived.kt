package rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.receive

import rocks.teagantotally.heartofgoldnotifications.domain.framework.MessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

class RecordMessageReceived(
    private val messageHistoryManager: MessageHistoryManager
) : UseCase<Message> {
    override suspend fun invoke(parameter: Message) {
        messageHistoryManager.recordMessageReceived(parameter)
    }
}
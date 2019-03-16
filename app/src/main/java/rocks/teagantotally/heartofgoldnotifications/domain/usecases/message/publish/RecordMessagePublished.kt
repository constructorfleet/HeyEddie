package rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.publish

import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.MessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

class RecordMessagePublished(
    private val messageHistoryManager: MessageHistoryManager
) : UseCase<Message> {
    override suspend fun invoke(parameter: Message) {
        messageHistoryManager.recordMessagePublished(parameter)
    }
}
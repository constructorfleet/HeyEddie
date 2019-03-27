package rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.receive

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.MessageHistoryManager
import rocks.teagantotally.kotqtt.domain.models.Message
import javax.inject.Inject

class RecordMessageReceived @Inject constructor(
    private val messageHistoryManager: MessageHistoryManager
) : UseCaseWithParameter<Message> {
    override suspend fun invoke(parameter: Message) {
        messageHistoryManager.recordMessageReceived(parameter)
    }
}
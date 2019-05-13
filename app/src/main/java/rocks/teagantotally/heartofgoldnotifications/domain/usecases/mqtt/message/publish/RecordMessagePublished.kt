package rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.message.publish

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.MessageHistoryManager
import rocks.teagantotally.kotqtt.domain.models.Message
import javax.inject.Inject

class RecordMessagePublished(
    private val messageHistoryManager: MessageHistoryManager
) : UseCaseWithParameter<Message> {
    override suspend fun invoke(parameter: Message) {
        messageHistoryManager.recordMessagePublished(parameter)
    }
}
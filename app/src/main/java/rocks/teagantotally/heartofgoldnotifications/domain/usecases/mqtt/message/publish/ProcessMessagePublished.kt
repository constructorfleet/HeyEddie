package rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.message.publish

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.MessagePublishedUseCase
import rocks.teagantotally.kotqtt.domain.models.Message

class ProcessMessagePublished(
    private val messageRecorder: RecordMessagePublished? = null,
    private vararg val messageProcessors: MessagePublishedUseCase<*>
) : UseCaseWithParameter<Message> {

    override suspend fun invoke(parameter: Message) {
        messageRecorder
            ?.invoke(parameter)

        messageProcessors
            .forEach { it(parameter) }
    }
}
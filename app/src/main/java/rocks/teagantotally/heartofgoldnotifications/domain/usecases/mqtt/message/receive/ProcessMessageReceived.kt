package rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.message.receive

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.MessageReceivedUseCase
import rocks.teagantotally.kotqtt.domain.models.Message

class ProcessMessageReceived(
    private vararg val messageProcessors: MessageReceivedUseCase<*>
) : UseCaseWithParameter<Message> {

    override suspend fun invoke(parameter: Message) {
        messageProcessors
            .forEach { it(parameter) }
    }
}
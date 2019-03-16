package rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.publish

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MqttEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

class ProcessMessagePublished(
    private vararg val messageProcessors: UseCase<Message>
) : UseCase<MqttEvent.MessagePublished> {

    override suspend fun invoke(parameter: MqttEvent.MessagePublished) {
        messageProcessors
            .forEach { it(parameter.message) }
    }
}
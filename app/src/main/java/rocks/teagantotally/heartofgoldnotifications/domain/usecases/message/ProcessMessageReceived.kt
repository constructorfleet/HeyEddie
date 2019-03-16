package rocks.teagantotally.heartofgoldnotifications.domain.usecases.message

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MqttEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

class ProcessMessageReceived(
    private vararg val messageProcessors: UseCase<Message>
) : UseCase<MqttEvent.MessageReceived> {

    override suspend fun invoke(parameter: MqttEvent.MessageReceived) {
        messageProcessors
            .forEach { it(parameter.message) }
    }
}
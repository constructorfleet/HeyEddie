package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrue
import rocks.teagantotally.heartofgoldnotifications.domain.framework.ProcessingUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MessageEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

class ProcessMessage(
    private vararg val messageProcessors: ProcessingUseCase<*, MessageEvent.Received>
) : UseCase<MessageEvent.Received> {

    override suspend fun invoke(parameter: MessageEvent.Received) {
        (parameter !is MessageEvent.Received.Failed)
            .ifTrue {
                messageProcessors
                    .forEach { it(parameter) }
            }
    }
}
package rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.message.receive

import com.google.gson.Gson
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.MessageReceivedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.MessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.ReceivedMessage
import rocks.teagantotally.kotqtt.domain.models.Message
import javax.inject.Inject

class RecordMessageReceived @Inject constructor(
    private val messageHistoryManager: MessageHistoryManager,
    gson: Gson
) : MessageReceivedUseCase<ReceivedMessage>(gson) {

    override fun handle(result: ReceivedMessage) {
        messageHistoryManager.recordMessageReceived(
            Message(
                result.topic,
                payload = result.body.toByteArray()
            )
        )
    }
}
package rocks.teagantotally.heartofgoldnotifications.domain.framework.event

import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.ReceivedMessage
import rocks.teagantotally.kotqtt.domain.models.Message

@Suppress("UNCHECKED_CAST")
abstract class MessageReceivedUseCase<ResultType : ReceivedMessage>(
    private val gson: Gson,
    private val messageType: MessageType = MessageType.ALL
) : UseCaseWithParameter<Message> {
    final override suspend fun invoke(parameter: Message) {
        try {
            gson.fromJson(String(parameter.payload), messageType.messageClass)
                ?.let { it as? ResultType }
                ?.let { handle(it) }
                ?: throw UnsupportedOperationException()
        } catch (throwable: Throwable) {
            Timber.w { "Unable to process $parameter as ${messageType.name}" }
        } catch (exception: Exception) {
            Timber.w { "Unable to process $parameter as ${messageType.name}" }
        }
    }

    protected abstract fun handle(result: ResultType)
}
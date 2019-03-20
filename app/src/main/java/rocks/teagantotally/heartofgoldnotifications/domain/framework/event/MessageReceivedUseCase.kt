package rocks.teagantotally.heartofgoldnotifications.domain.framework.event

import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.ReceivedMessage

@Suppress("UNCHECKED_CAST")
abstract class MessageReceivedUseCase<ResultType : ReceivedMessage>(
    private val gson: Gson,
    private val messageType: MessageType = MessageType.ALL
) : UseCase<Message> {
    final override suspend fun invoke(parameter: Message) {
        try {
            gson.fromJson(parameter.payload, messageType.messageClass)
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
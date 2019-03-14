package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.ProcessingUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MessageEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage

class NotifyUseCase(
    private val gson: Gson,
    private val notifier: Notifier
) : ProcessingUseCase<MessageEvent.Received, MessageEvent>(MessageEvent.Received::class) {
    override suspend fun handle(parameter: MessageEvent.Received) {
        try {
            gson.fromJson(parameter.message.payload, NotificationMessage::class.java)
                .let { notifier.notify(it) }
        } catch (throwable: Throwable) {
            Timber.e(throwable)
        }
    }
}
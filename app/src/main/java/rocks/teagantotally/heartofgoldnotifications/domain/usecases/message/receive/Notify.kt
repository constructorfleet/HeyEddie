package rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.receive

import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage

class Notify(
    private val gson: Gson,
    private val notifier: Notifier
) : UseCase<Message> {
    override suspend fun invoke(parameter: Message) {
        try {
            gson.fromJson(parameter.payload, NotificationMessage::class.java)
                ?.let { notifier.notify(it) }
        } catch (throwable: Throwable) {
            Timber.w { "Message is not a notification" }
        }
    }
}
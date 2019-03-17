package rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.receive

import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.MessageReceivedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage

class Notify(
    gson: Gson,
    private val notifier: Notifier
) : MessageReceivedUseCase<NotificationMessage>(
    gson,
    MessageType.NOTIFICATION
) {
    override fun handle(result: NotificationMessage) {
        notifier.notify(result)
    }
}
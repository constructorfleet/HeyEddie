package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import rocks.teagantotally.heartofgoldnotifications.domain.models.ReceivedMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessage

class ConvertToNotificationMessageUseCase(
    private val gson: Gson
) : UseCase<ReceivedMessage, NotificationMessage?>() {
    override suspend fun invoke(params: ReceivedMessage): NotificationMessage? =
        try {
            gson.fromJson<NotificationMessage>(String(params.payload), NotificationMessage::class.java)
        } catch (e: JsonSyntaxException) {
            Timber.e(e)
            null
        }
}
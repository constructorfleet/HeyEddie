package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import com.google.gson.Gson
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseResult
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessage
import java.lang.IllegalArgumentException

class ConvertToNotificationMessageUseCase(
    private val gson: Gson
) : UseCase<Message, NotificationMessage> {
    override suspend fun invoke(parameter: Message): UseCaseResult<NotificationMessage> =
        try {
            gson.fromJson<NotificationMessage>(parameter.payload, NotificationMessage::class.java)
                .let { UseCaseResult.Success(it) }
        } catch (t: Throwable) {
            UseCaseResult.Failure(IllegalArgumentException())
        }
}
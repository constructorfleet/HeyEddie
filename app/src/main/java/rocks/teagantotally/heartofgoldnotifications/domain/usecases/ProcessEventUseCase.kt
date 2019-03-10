package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import kotlinx.android.parcel.Parceler
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientMessageReceive
import rocks.teagantotally.heartofgoldnotifications.domain.processors.notifications.Notifier

class ProcessEventUseCase(
    private val notifier: Notifier,
    private val gson: Gson
) : UseCase<ClientEvent, Unit>() {
    override suspend fun invoke(params: ClientEvent) {
        when (params) {
            is ClientMessageReceive.Successful ->
                try {
                    gson.fromJson(
                        params.message.payload,
                        NotificationMessage::class.java
                    ).let {
                        notifier.notify(it)
                    }
                } catch (t: Throwable) {
                    Timber.e(t)
                }
                else -> return
        }
    }
}
package rocks.teagantotally.heartofgoldnotifications.data.services

import android.app.IntentService
import android.content.Intent
import com.github.ajalt.timberkt.Timber
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage
import javax.inject.Inject

class NotificationService : IntentService(NAME) {

    companion object {
        const val NAME = "rocks.teagantotally.heartofgoldnotifications.data.services.NotificationService"

        object Notify {
            const val ACTION = "notify"
            const val KEY_NOTIFICATION = "message"
        }

        object Dismiss {
            const val ACTION = "dismiss"
            const val KEY_NOTIFICATION_ID = "id"
        }
    }

    @Inject
    lateinit var notifier: Notifier

    override fun onCreate() {
        super.onCreate()
        HeyEddieApplication.applicationComponent.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        intent
            ?.run {
                when (action) {
                    Dismiss.ACTION ->
                        getIntExtra(Dismiss.KEY_NOTIFICATION_ID, 0)
                            .let { notifier.dismiss(it) }
                    Notify.ACTION ->
                        getParcelableExtra<NotificationMessage>(Notify.KEY_NOTIFICATION)
                            ?.let { notifier.notify(it) }
                            ?: Timber.w { "Missing required field: ${Notify.KEY_NOTIFICATION}" }
                    else -> Timber.w { "Unable to handle action: $action" }
                }
            } ?: Timber.w { "No intent provide" }
    }
}
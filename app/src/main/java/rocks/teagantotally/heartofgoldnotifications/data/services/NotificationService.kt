package rocks.teagantotally.heartofgoldnotifications.data.services

import android.app.IntentService
import android.content.Intent
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.domain.models.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.processors.notifications.Notifier
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class NotificationService : IntentService(NAME), Scoped {

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

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext by lazy { job.plus(Dispatchers.Main) }

    override fun onCreate() {
        super.onCreate()
        HeyEddieApplication.applicationComponent.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        launch {
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

    override fun onDestroy() {
        super.onDestroy()
        job.cancelChildren()
    }
}
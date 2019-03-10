package rocks.teagantotally.heartofgoldnotifications.data.services

import android.app.*
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.IBinder
import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifApply
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrue
import rocks.teagantotally.heartofgoldnotifications.domain.clients.MqttClient
import rocks.teagantotally.heartofgoldnotifications.domain.models.*
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
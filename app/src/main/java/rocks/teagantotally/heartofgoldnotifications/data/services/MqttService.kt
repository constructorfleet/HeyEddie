package rocks.teagantotally.heartofgoldnotifications.data.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.clients.Client
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MqttService : Service(), Scoped {

    @Inject
    lateinit var client: Client

    @Inject
    lateinit var configProvider: ConnectionConfigProvider

    @Inject
    lateinit var channelManager: ChannelManager

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext =
        job.plus(Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? =
        null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
        Service.START_STICKY

    override fun onDestroy() {
        sendBroadcast(
            Intent(
                this,
                RestartReceiver::class.java
            )
        )
    }

    class RestartReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.let {
                it.startService(
                    Intent(
                        it,
                        MqttService::class.java
                    )
                )
            }
        }
    }
}
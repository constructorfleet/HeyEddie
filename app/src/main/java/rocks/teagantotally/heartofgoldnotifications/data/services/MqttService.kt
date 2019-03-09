package rocks.teagantotally.heartofgoldnotifications.data.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.SubComponent
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.clients.Client
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientModule
import rocks.teagantotally.heartofgoldnotifications.domain.models.*
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

    private val eventChannel: BroadcastChannel<ClientEvent> by lazy { channelManager.eventChannel }
    private val messageChannel: BroadcastChannel<MessageEvent> by lazy { channelManager.messageChannel }
    private val connectChannel: Channel<ConnectEvent> by lazy { channelManager.connectChannel }
    private val notifyChannel: Channel<String> by lazy { channelManager.notifyChannel }

    override fun onBind(intent: Intent?): IBinder? =
        null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
        HeyEddieApplication.clientComponent
            .let {
                when (it) {
                    is SubComponent.Initialized -> it.component
                    is SubComponent.NotInitialized ->
                        HeyEddieApplication.setClient(ClientModule(this))
                }.let { it.inject(this) }
            }
            .run { listenForEvents() }
            .run { START_STICKY }

    override fun onDestroy() {
        sendBroadcast(
            Intent(
                this,
                RestartReceiver::class.java
            )
        )
    }

    private fun listenForEvents() {
        launch {
            while (!connectChannel.isClosedForReceive) {
                connectChannel.receiveOrNull().let {
                    when (it) {
                        is ConnectEvent.Connect -> client.connect(configProvider.getConnectionConfiguration())
                        is ConnectEvent.Disconnect -> client.disconnect()
                    }
                }
            }
        }
        launch {
            while (true) {
                eventChannel.consumeEach {
                    when (it.type) {
                        is ClientEventType.Connection -> {
//                            notifyChannel.send("CONNECTED")
                            client.subscribe("/test", 0)
                        }
                    }
                }
            }
        }

        launch {
            while (true) {
                messageChannel.consumeEach {
                    when (it) {
                        is MessageEvent.Received.Success -> notifyChannel.send(String(it.message.payload))
                        is MessageEvent.Received.Failure -> Timber.d(it.throwable)
                        else -> return@consumeEach
                    }
                }
            }
        }
    }

    class RestartReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
//            try {
//                context?.let {
//                    it.startService(
//                        Intent(
//                            it,
//                            MqttService::class.java
//                        )
//                    )
//                }
//            } catch (t: Throwable) {
//                Timber.e(t)
//            }
        }
    }
}
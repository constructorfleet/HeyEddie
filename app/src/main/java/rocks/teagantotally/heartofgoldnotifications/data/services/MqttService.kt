package rocks.teagantotally.heartofgoldnotifications.data.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.SubComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.IODispatcher
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrue
import rocks.teagantotally.heartofgoldnotifications.common.extensions.safeLet
import rocks.teagantotally.heartofgoldnotifications.data.services.helpers.LongRunningServiceConnection
import rocks.teagantotally.heartofgoldnotifications.data.services.helpers.ServiceBinder
import rocks.teagantotally.heartofgoldnotifications.domain.clients.Client
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientModule
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientMessagePublish
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.CommandEvent
import rocks.teagantotally.heartofgoldnotifications.domain.processors.notifications.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.ProcessEventUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MqttService : Service(), Scoped {

    companion object {
        lateinit var serviceBinder: ServiceBinder<MqttService>
        val longRunningServiceConnection: LongRunningServiceConnection<MqttService> =
            LongRunningServiceConnection()
        const val KEY_TOPIC = "topic"
        const val KEY_PAYLOAD = "payload"
    }

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext = job.plus(Dispatchers.IO)
    @Inject
    lateinit var client: Client

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
            .run { serviceBinder = ServiceBinder(this@MqttService) }
            .run {
                bindService(
                    Intent(this@MqttService, MqttService::class.java),
                    longRunningServiceConnection,
                    Context.BIND_AUTO_CREATE
                )
            }
            .run { client.connect() }
            .run { startService(Intent(this@MqttService, EventService::class.java)) }
            .run { START_STICKY }

    override fun onDestroy() {
        super.onDestroy()
        sendBroadcast(
            Intent(
                this,
                RestartReceiver::class.java
            )
        )
        job.cancelChildren()
    }

    class PublishReceiver : BroadcastReceiver(), CoroutineScope {
        companion object {
            const val KEY_MESSAGE = "message"
            const val KEY_NOTIFICATION_ID = "notification_id"
        }

        @Inject
        lateinit var channelManager: ChannelManager

        @Inject
        lateinit var notifier: Notifier

        override val coroutineContext: CoroutineContext = Job().plus(Dispatchers.IO)

        private val commandChannel: Channel<CommandEvent> by lazy { channelManager.commandChannel }
        private val eventChannel: BroadcastChannel<ClientEvent> by lazy { channelManager.eventChannel }

        override fun onReceive(context: Context?, intent: Intent?) {
            HeyEddieApplication.applicationComponent.inject(this)
            intent
                ?.apply {
                    getIntExtra(KEY_NOTIFICATION_ID, 0)
                        .ifTrue({it != 0}) {
                            notifier.dismiss(it)
                        }
                    getParcelableExtra<Message>(KEY_MESSAGE)
                        ?.let {
                            launch {
                                if (!commandChannel.isClosedForSend) {
                                    commandChannel.send(
                                        CommandEvent.Publish(
                                            it
                                        )
                                    )
                                } else if (!eventChannel.isClosedForSend) {
                                    eventChannel.send(
                                        ClientMessagePublish.Failed(
                                            null,
                                            it,
                                            Throwable("Cannot communicate with client")
                                        )
                                    )
                                } else {
                                    Timber.w { "Cannot process publish command" }
                                }
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



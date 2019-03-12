package rocks.teagantotally.heartofgoldnotifications.data.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.SubComponent
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrue
import rocks.teagantotally.heartofgoldnotifications.data.managers.transform
import rocks.teagantotally.heartofgoldnotifications.data.services.helpers.LongRunningServiceConnection
import rocks.teagantotally.heartofgoldnotifications.data.services.helpers.ServiceBinder
import rocks.teagantotally.heartofgoldnotifications.domain.clients.Client
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientModule
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientMessagePublish
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.CommandEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Event
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.NotificationActivated
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.ProcessEventUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.UpdatePersistentNotificationUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MqttService : Service(), Scoped {

    companion object {
        const val ACTION_START = "rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.start"

        lateinit var serviceBinder: ServiceBinder<MqttService>
        val longRunningServiceConnection: LongRunningServiceConnection<MqttService> =
            LongRunningServiceConnection()
    }

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext = job.plus(Dispatchers.IO)

    @Inject
    lateinit var channelManager: ChannelManager

    @Inject
    lateinit var notifier: Notifier

    @Inject
    lateinit var eventProcessor: ProcessEventUseCase

    private val eventChannel: ReceiveChannel<Event> by lazy { channelManager.eventChannel.openSubscription() }
    private val commandChannel: ReceiveChannel<CommandEvent> by lazy { channelManager.commandChannel.openSubscription() }
    private lateinit var client: Client

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
            .run {
                UpdatePersistentNotificationUseCase
                    .getPersistentNotification(false)
                    .let {
                        notifier.notify(it)
                        launch {
                            it.transform(this@MqttService)
                                .let {
                                    startForeground(it.first, it.second)
                                }
                        }
                    }
            }
            .run { listen() }
            .run { START_STICKY }

    override fun onDestroy() {
        super.onDestroy()
        job.cancelChildren()
        sendBroadcast(
            Intent(
                this,
                CommandReceiver::class.java
            ).apply { action = ACTION_START }
        )
    }

    private fun listen() {
        launch {
            while (!commandChannel.isClosedForReceive) {
                commandChannel.consumeEach {
                    if (it == CommandEvent.Connect && !this@MqttService::client.isInitialized) {
                        (HeyEddieApplication.getClient() as? SubComponent.Initialized)
                            ?.let { client = it.component.provideClient() }
                            ?.let { client.connect() }
                    }
                }
            }
        }
        launch {
            while (!eventChannel.isClosedForReceive) {
                eventChannel.consumeEach {
                    eventProcessor(it)
                }
            }
        }
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

        private val commandChannel: BroadcastChannel<CommandEvent> by lazy { channelManager.commandChannel }
        private val eventChannel: BroadcastChannel<Event> by lazy { channelManager.eventChannel }

        override fun onReceive(context: Context?, intent: Intent?) {
            HeyEddieApplication.applicationComponent.inject(this)
            intent
                ?.apply {
                    launch {
                        getIntExtra(KEY_NOTIFICATION_ID, 0)
                            .ifTrue({ it != 0 }) {
                                if (!eventChannel.isClosedForSend) {
                                    eventChannel.send(NotificationActivated(it))
                                }
                            }
                        getParcelableExtra<Message>(KEY_MESSAGE)
                            ?.let {

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

    class CommandReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                context?.let {
                    it.startForegroundService(
                        Intent(
                            it,
                            MqttService::class.java
                        )
                    )
                }
            } catch (t: Throwable) {
                Timber.e(t)
            }
        }
    }
}



package rocks.teagantotally.heartofgoldnotifications.data.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
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
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.ClientCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.ConnectionCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.NotificationCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ConnectionEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MessageEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.ProcessMessage
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.UpdatePersistentNotificationUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
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
    lateinit var processMessage: ProcessMessage

    @Inject
    lateinit var notifier: Notifier

    private val connectionCommandChannel: ReceiveChannel<ConnectionCommand> by lazy { channelManager.connectionCommandChannel.openSubscription() }
    private val connectionEventChannel: ReceiveChannel<ConnectionEvent> by lazy { channelManager.connectionEventChannel.openSubscription() }
    private val messageEventChannel: ReceiveChannel<MessageEvent> by lazy { channelManager.messageEventChannel.openSubscription() }
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
            while (!connectionCommandChannel.isClosedForReceive) {
                connectionCommandChannel.consumeEach {
                    if (it == ConnectionCommand.Connect && !this@MqttService::client.isInitialized) {
                        (HeyEddieApplication.getClient() as? SubComponent.Initialized)
                            ?.let { client = it.component.provideClient() }
                            ?.let { client.connect() }
                        // TODO : Failure
                    }
                }
            }
        }
        launch {
            while (!connectionEventChannel.isClosedForReceive) {
                connectionEventChannel.consumeEach {
                    notifier.notify(UpdatePersistentNotificationUseCase.getPersistentNotification(it.isConnected))
                }
            }
        }
        launch {
            while(!messageEventChannel.isClosedForReceive) {
                messageEventChannel.consumeEach {
                    if(it is MessageEvent.Received) {
                        processMessage(it)
                    }
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

        private val clientCommandChannel: SendChannel<ClientCommand> by lazy { channelManager.clientCommandChannel }
        private val messageEventChannel: SendChannel<MessageEvent> by lazy { channelManager.messageEventChannel }
        private val notificationCommandChannel: SendChannel<NotificationCommand> by lazy { channelManager.notificationCommandChannel }

        override fun onReceive(context: Context?, intent: Intent?) {
            HeyEddieApplication.applicationComponent.inject(this)
            intent
                ?.apply {
                    launch {
                        getIntExtra(KEY_NOTIFICATION_ID, 0)
                            .ifTrue({ it != 0 }) {
                                if (!notificationCommandChannel.isClosedForSend) {
                                    notificationCommandChannel.send(NotificationCommand.Dismiss(it))
                                }
                            }
                        getParcelableExtra<Message>(KEY_MESSAGE)
                            ?.let {
                                ClientCommand.Publish(
                                    it
                                ).let { command ->
                                    if (!clientCommandChannel.isClosedForSend) {
                                        clientCommandChannel.send(
                                            command
                                        )
                                    } else if (!messageEventChannel.isClosedForSend) {
                                        messageEventChannel.send(
                                            MessageEvent.Published.Failed(
                                                command,
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



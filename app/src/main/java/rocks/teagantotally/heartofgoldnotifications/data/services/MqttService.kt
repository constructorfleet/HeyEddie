package rocks.teagantotally.heartofgoldnotifications.data.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.*
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrue
import rocks.teagantotally.heartofgoldnotifications.data.managers.transform
import rocks.teagantotally.heartofgoldnotifications.data.services.helpers.LongRunningServiceConnection
import rocks.teagantotally.heartofgoldnotifications.data.services.helpers.ServiceBinder
import rocks.teagantotally.heartofgoldnotifications.domain.clients.Client
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientModule
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.UpdatePersistentNotificationUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MqttService : Service(), Client.ConnectionListener, Scoped {

    companion object {
        private const val ACTION_BASE = "rocks.teagantotally.heartofgoldnotifications.data.services.MqttService"
        const val ACTION_START = "$ACTION_BASE.start"
        const val ACTION_CONNECT = "$ACTION_BASE.connect"
        const val ACTION_DISCONNECT = "$ACTION_BASE.disconnect"
        const val ACTION_PUBLISH = "$ACTION_BASE.publish"
        const val ACTION_SUBSCRIBE = "$ACTION_BASE.subscribe"
        const val ACTION_UNSUBSCRIBE = "$ACTION_BASE.unsubscribe"

        private val COMMAND_ACTIONS =
            listOf(
                ACTION_START,
                ACTION_CONNECT,
                ACTION_DISCONNECT,
                ACTION_PUBLISH,
                ACTION_SUBSCRIBE,
                ACTION_UNSUBSCRIBE
            )

        const val EXTRA_MESSAGE = "message"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_TOPIC = "topic"
        const val EXTRA_QOS = "qos"

        lateinit var serviceBinder: ServiceBinder<MqttService>
        val longRunningServiceConnection: LongRunningServiceConnection<MqttService> =
            LongRunningServiceConnection()
    }

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext = job.plus(Dispatchers.IO)

    @Inject
    lateinit var notifier: Notifier
    @Inject
    lateinit var updatePersistentNotification: UpdatePersistentNotificationUseCase

    private var client: Client? = null
    private val commandReceiver: MqttService.CommandReceiver = MqttService.CommandReceiver(this)

    override fun onBind(intent: Intent?): IBinder? =
        serviceBinder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
        HeyEddieApplication.applicationComponent.inject(this)
            .run { serviceBinder = ServiceBinder(this@MqttService) }
            .run {
                bindService(
                    Intent(this@MqttService, MqttService::class.java),
                    longRunningServiceConnection,
                    Context.BIND_AUTO_CREATE
                )
            }
            .run {
                registerReceiver(
                    commandReceiver,
                    IntentFilter()
                        .apply {
                            COMMAND_ACTIONS
                                .forEach { addAction(it) }
                        }
                )
            }
            .run {
                launch {
                    updatePersistentNotification(Client.ConnectionState.Unknown)
                }
            }
            .run {
                UpdatePersistentNotificationUseCase.getPersistentNotification(Client.ConnectionState.Unknown)
                    .transform(this@MqttService)
                    .let { startForeground(it.first, it.second) }
            }
            .run { START_STICKY }

    override fun onDestroy() {
        super.onDestroy()
        client?.removeConnectionListener(this)
        unregisterReceiver(commandReceiver)
        job.cancelChildren()
        sendBroadcast(
            Intent(
                this,
                StartReceiver::class.java
            ).apply { action = ACTION_START }
        )
    }

    override fun onConnectionChange(state: Client.ConnectionState) {
        launch {
            updatePersistentNotification(state)
        }
    }

    internal fun connect() {
        // Disconnect if already connected
        client?.disconnect()

        client =
            HeyEddieApplication
                .applicationComponent
                .clientComponentBuilder()
                .clientModule(ClientModule(this))
                .build()
                .provideClient()
        client?.let {
            it.addConnectionListener(this)
            launch {
                it.connect()
            }
        }
    }

    internal fun disconnect() {
        launch {
            client?.disconnect()
        }
    }

    internal fun dismissNotification(notificationId: Int) {
        launch {
            notifier.dismiss(notificationId)
        }
    }

    internal fun publish(message: Message) {
        launch {
            client?.publish(message)
        }
    }

    internal fun subscribe(topic: String, qos: Int) {
        launch {
            client?.subscribe(topic, qos)
        }
    }

    internal fun unsubscribe(topic: String) {
        launch {
            client?.unsubscribe(topic)
        }
    }

    internal class CommandReceiver(private val service: MqttService) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                intent?.apply {
                    when (action) {
                        ACTION_CONNECT -> service.connect()
                        ACTION_DISCONNECT -> service.disconnect()
                        ACTION_PUBLISH -> {
                            getIntExtra(EXTRA_NOTIFICATION_ID, 0)
                                .ifTrue({ it > 0 }) { service.dismissNotification(it) }

                            getParcelableExtra<Message>(EXTRA_MESSAGE)
                                ?.let { service.publish(it) }
                        }
                        ACTION_SUBSCRIBE ->
                            service.subscribe(
                                getStringExtra(EXTRA_TOPIC),
                                getIntExtra(EXTRA_QOS, 0)
                            )
                        ACTION_UNSUBSCRIBE ->
                            service.unsubscribe(
                                getStringExtra(EXTRA_TOPIC)
                            )
                        else -> null
                    } ?: throw IllegalArgumentException()
                }
            } catch (t: Throwable) {
                Timber.e(t) { "Unable to process action ${intent?.action}" }
            }
        }
    }

    class StartReceiver : BroadcastReceiver() {
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



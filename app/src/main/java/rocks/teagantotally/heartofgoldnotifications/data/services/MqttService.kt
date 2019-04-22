package rocks.teagantotally.heartofgoldnotifications.data.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientContainer
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrue
import rocks.teagantotally.heartofgoldnotifications.common.extensions.unique
import rocks.teagantotally.heartofgoldnotifications.data.managers.transform
import rocks.teagantotally.heartofgoldnotifications.data.services.helpers.LongRunningServiceConnection
import rocks.teagantotally.heartofgoldnotifications.data.services.helpers.ServiceBinder
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.SubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.ClientState
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.NotificationCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.SubscriptionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientConfigurationChangedEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessage
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.NotificationMessageChannel
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.FinishNotifyUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.UpdatePersistentNotificationUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.config.ClientConfigurationSavedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.config.GetClientConfigurationUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.connection.ConnectClient
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.MqttEventProcessor
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.message.publish.PublishMessage
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.message.receive.Notify
import rocks.teagantotally.kotqtt.domain.models.Message
import rocks.teagantotally.kotqtt.domain.models.commands.MqttPublishCommand
import rocks.teagantotally.kotqtt.domain.models.events.MqttEvent
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MqttService : Service(),
    CoroutineScope,
    SubscriptionManager.Listener {

    companion object {
        private const val BASE = "rocks.teagantotally.heartofgoldnotifications.data.services.MqttService"
        private const val BASE_ACTION = "$BASE.action"
        private const val BASE_EVENT = "$BASE.event"

        const val ACTION_START = "$BASE_ACTION.start"
        const val ACTION_PUBLISH = "$BASE_ACTION.publish"
        const val ACTION_DISMISS = "$BASE_ACTION.dismiss"

        const val EXTRA_MESSAGE = "message"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_NOTIFICATION_AUTO_DISMISSED = "notification_auto_dismissed"

        lateinit var serviceBinder: ServiceBinder<MqttService>
        val longRunningServiceConnection: LongRunningServiceConnection<MqttService> =
            LongRunningServiceConnection()

        private val debugChannelId: String = "Debug"
    }


    private val coroutineScope: CoroutineScope by lazy { HeyEddieApplication.applicationComponent.provideUICoroutineScope() }
    override val coroutineContext: CoroutineContext by lazy { coroutineScope.coroutineContext }

    @Inject
    lateinit var notifier: Notifier
    @Inject
    lateinit var clientConfigurationChangedUseCase: ClientConfigurationSavedUseCase
    @Inject
    lateinit var getClientConfiguration: GetClientConfigurationUseCase
    @Inject
    lateinit var mqttEventProcessor: MqttEventProcessor

    private val clientConfigurationChanged: ReceiveChannel<ClientConfigurationChangedEvent>
        get() = clientConfigurationChangedUseCase.openSubscription()
    private lateinit var clientContainer: ClientContainer
    private val connect: ConnectClient?
        get() = clientContainer?.connectClient
    private lateinit var mqttEventConsumer: ReceiveChannel<MqttEvent>
    private var publishReceiver: PublishReceiver? = null
    private var dismissReceiver: DismissNotificationReceiver? = null

    override fun onBind(intent: Intent?): IBinder? =
        serviceBinder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
        HeyEddieApplication.applicationComponent
            .also { it.inject(this) }
            .run {
                runBlocking {
                    notifier.createChannel(UpdatePersistentNotificationUseCase.PERSISTENT_CHANNEL)
                }.run {
                    UpdatePersistentNotificationUseCase.getPersistentNotification(ClientState.Disconnected)
                        .transform(this@MqttService, false)
                        .let { startForeground(it.first, it.second) }
                }.run {
                    launch {
                        getClientConfiguration()
                            ?.let { onClientConfigured(it) }
                            .run { listenForConfigurationChange() }
                    }
                }.run {
                    serviceBinder = ServiceBinder(this@MqttService)
                }.run {
                    dismissReceiver =
                        DismissNotificationReceiver(this@MqttService)
                            .also { registerReceiver(it, IntentFilter(ACTION_DISMISS)) }
                }.run {
                    bindService(
                        Intent(this@MqttService, MqttService::class.java),
                        longRunningServiceConnection,
                        Context.BIND_AUTO_CREATE
                    )
                }.run { START_STICKY }
            }


    override fun onDestroy() {
        publishReceiver?.let { unregisterReceiver(it) }
        dismissReceiver?.let { unregisterReceiver(it) }
        sendBroadcast(
            Intent(
                this,
                StartReceiver::class.java
            ).apply {
                action =
                    ACTION_START
            }
        )
        super.onDestroy()
    }

    private fun listenForConfigurationChange() {
        launch {
            clientConfigurationChanged
                .let { channel ->
                    while (!channel.isClosedForReceive) {
                        channel.receiveOrNull()?.let {
                            onClientConfigured(it.new)
                        }
                    }
                }
        }
    }

    private suspend fun onClientConfigured(clientConfiguration: ConnectionConfiguration) {
        HeyEddieApplication
            .clientComponent
            ?.provideClientContainer()
            ?.also {
                clientContainer = it
                clientContainer
                    .eventProducer
                    .subscribe()
                    .let { mqttEventConsumer = it }
                    .run { listenForEvents() }
            }
            ?.run {
                publishReceiver?.let { unregisterReceiver(it) }
                PublishReceiver(this@MqttService)
                    .also { registerReceiver(it, IntentFilter(ACTION_PUBLISH)) }
                    .let { publishReceiver = it }
            }
            ?.ifTrue({ clientConfiguration.autoReconnect }) {
                connect?.invoke()
            }
    }

    private fun listenForEvents() {
        launch {
            while (!mqttEventConsumer.isClosedForReceive) {
                mqttEventConsumer.receiveOrNull()?.let {
                    mqttEventProcessor(it)
                }
            }
        }
    }

    override fun onSubscriptionAdded(subscription: SubscriptionConfiguration) {
    }

    override fun onSubscriptionRemoved(subscription: SubscriptionConfiguration) {
    }

    class DismissNotificationReceiver(
        coroutineScope: CoroutineScope
    ) : BroadcastReceiver(), CoroutineScope by coroutineScope {
        @Inject
        lateinit var finishNotify: FinishNotifyUseCase
        @Inject
        lateinit var notify: Notify
        @Inject
        lateinit var gson: Gson

        override fun onReceive(context: Context?, intent: Intent?) {
            HeyEddieApplication.applicationComponent.inject(this)
            intent?.let {
                when (it.action) {
                    ACTION_DISMISS ->
                        launch {
                            finishNotify(
                                NotificationCommand.Dismiss(
                                    it.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
                                )
                            )
                            it.getBooleanExtra(EXTRA_NOTIFICATION_AUTO_DISMISSED, false)
                                .ifTrue {
                                    NotificationMessage(
                                        NotificationMessageChannel(
                                            debugChannelId,
                                            debugChannelId,
                                            "Debugging"
                                        ),
                                        Int.unique(),
                                        "Notification auto dismissed",
                                        "Notification ${it.getIntExtra(EXTRA_NOTIFICATION_ID, 0)}",
                                        false,
                                        false,
                                        false
                                    )
                                        .let { gson.toJson(it) }
                                        .let { Message("", payload = it.toByteArray()) }
                                        .let { notify(it) }
                                }

                        }
                    else -> return
                }
            }
        }
    }

    class PublishReceiver(
        coroutineScope: CoroutineScope
    ) : BroadcastReceiver(), CoroutineScope by coroutineScope {
        @Inject
        lateinit var publishMessage: PublishMessage

        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                HeyEddieApplication
                    .clientComponent
                    ?.inject(this)
                    ?.let { intent }
                    ?.let {
                        when (it.action) {
                            ACTION_PUBLISH ->
                                it.getParcelableExtra<Message>(EXTRA_MESSAGE)
                                    ?.let { message ->
                                        launch {
                                            publishMessage(MqttPublishCommand(message))
                                        }
                                    }
                                    .run {
                                        context?.sendBroadcast(
                                            Intent(ACTION_DISMISS)
                                                .putExtra(
                                                    EXTRA_NOTIFICATION_ID,
                                                    it.getIntExtra(
                                                        EXTRA_NOTIFICATION_ID,
                                                        0
                                                    )
                                                )
                                        )
                                    }
                            else -> return
                        }
                    }

            } catch (t: Throwable) {
                Timber.e(t)
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



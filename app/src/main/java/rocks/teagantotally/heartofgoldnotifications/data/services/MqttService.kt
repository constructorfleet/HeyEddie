package rocks.teagantotally.heartofgoldnotifications.data.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientContainer
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifAlso
import rocks.teagantotally.heartofgoldnotifications.data.managers.transform
import rocks.teagantotally.heartofgoldnotifications.data.services.helpers.LongRunningServiceConnection
import rocks.teagantotally.heartofgoldnotifications.data.services.helpers.ServiceBinder
import rocks.teagantotally.heartofgoldnotifications.domain.framework.Notifier
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.ClientConfigurationChangedEvent
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.SubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.ClientState
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.SubscriptionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.UpdatePersistentNotificationUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.config.ClientConfigurationChangedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.config.GetClientConfigurationUseCase
import rocks.teagantotally.kotqtt.domain.framework.client.CommandResult
import rocks.teagantotally.kotqtt.domain.models.events.MqttConnectedEvent
import rocks.teagantotally.kotqtt.domain.models.events.MqttDisconnectedEvent
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
        const val ACTION_CONNECT = "$BASE_ACTION.connect"
        const val ACTION_DISCONNECT = "$BASE_ACTION.disconnect"
        const val ACTION_PUBLISH = "$BASE_ACTION.publish"
        const val ACTION_SUBSCRIBE = "$BASE_ACTION.subscribe"
        const val ACTION_UNSUBSCRIBE = "$BASE_ACTION.unsubscribe"

        const val EVENT_CONNECTED = "$BASE_EVENT.connected"
        const val EVENT_DISCONNECTED = "$BASE_EVENT.disconnected"
        const val EVENT_SUBSCRIBED = "$BASE_EVENT.subscribed"
        const val EVENT_UNSUBSCRIBED = "$BASE_EVENT.unsubscribed"
        const val EVENT_MESSAGE_PUBLISHED = "$BASE_EVENT.message_published"
        const val EVENT_MESSAGE_RECEIVED = "$BASE_EVENT.message_received"
        const val EVENT_COMMAND_FAILED = "$BASE_EVENT.command_failed"

        private val COMMAND_ACTIONS =
            listOf(
                ACTION_START,
                ACTION_CONNECT,
                ACTION_DISCONNECT,
                ACTION_PUBLISH,
                ACTION_SUBSCRIBE,
                ACTION_UNSUBSCRIBE
            )

        private val EVENTS =
            listOf(
                EVENT_CONNECTED,
                EVENT_DISCONNECTED,
                EVENT_SUBSCRIBED,
                EVENT_UNSUBSCRIBED,
                EVENT_MESSAGE_RECEIVED,
                EVENT_MESSAGE_PUBLISHED,
                EVENT_COMMAND_FAILED
            )

        const val EXTRA_MESSAGE = "message"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_TOPIC = "topic"
        const val EXTRA_QOS = "qos"
        const val EXTRA_COMMAND = "command"
        const val EXTRA_FAILURE_REASON = "failure_reason"

        lateinit var serviceBinder: ServiceBinder<MqttService>
        val longRunningServiceConnection: LongRunningServiceConnection<MqttService> =
            LongRunningServiceConnection()
    }


    private val coroutineScope: CoroutineScope by lazy { HeyEddieApplication.applicationComponent.provideUICoroutineScope() }
    override val coroutineContext: CoroutineContext by lazy { coroutineScope.coroutineContext }

    @Inject
    lateinit var notifier: Notifier
    @Inject
    lateinit var updatePersistentNotification: UpdatePersistentNotificationUseCase
    @Inject
    lateinit var clientConfigurationChangedUseCase: ClientConfigurationChangedUseCase
    @Inject
    lateinit var getClientConfiguration: GetClientConfigurationUseCase

    private val clientConfigurationChanged: ReceiveChannel<ClientConfigurationChangedEvent> by lazy { clientConfigurationChangedUseCase.openSubscription() }
    private lateinit var mqttEventConsumer: ReceiveChannel<MqttEvent>
    private lateinit var clientUseCases: ClientContainer

    override fun onBind(intent: Intent?): IBinder? =
        serviceBinder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
        HeyEddieApplication.applicationComponent
            .also { it.inject(this) }
            .run {
                runBlocking {
                    notifier.createChannel(UpdatePersistentNotificationUseCase.PERSISTENT_CHANNEL)
                }
                    .run {
                        UpdatePersistentNotificationUseCase.getPersistentNotification(ClientState.Disconnected)
                            .transform(this@MqttService)
                            .let { startForeground(it.first, it.second) }
                    }
                    .run {
                        launch {
                            getClientConfiguration()
                                ?.let { onClientConfigured(it) }
                                ?: listenForConfigurationChange()
                        }
                    }
                    .run { serviceBinder = ServiceBinder(this@MqttService) }
                    .run {
                        bindService(
                            Intent(this@MqttService, MqttService::class.java),
                            longRunningServiceConnection,
                            Context.BIND_AUTO_CREATE
                        )
                    }
                    .run { START_STICKY }
            }


    override fun onDestroy() {
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
            while (!clientConfigurationChanged.isClosedForReceive) {
                clientConfigurationChanged.receiveOrNull()?.let {
                    onClientConfigured(it.connectionConfiguration)
                }
            }
        }
    }

    private suspend fun onClientConfigured(clientConfiguration: ConnectionConfiguration) {
        HeyEddieApplication.clientComponent.provideClientContainer()
            .also {
                mqttEventConsumer = it.eventProducer.subscribe()
                listenForEvents()
            }
            .ifAlso({ clientConfiguration.autoReconnect }) {
                HeyEddieApplication
                    .clientComponent
                    .provideClientContainer()
                    .connectClient()
            }
    }

    private fun listenForEvents() {
        launch {
            while (!mqttEventConsumer.isClosedForReceive) {
                mqttEventConsumer.receiveOrNull()?.let { consume(it) }
            }
        }
    }

    private suspend fun consume(event: MqttEvent) {
        when ((event as? CommandResult.Success<*, *>)?.let { it.result } ?: event) {
            MqttConnectedEvent ->
                updatePersistentNotification(ClientState.Connected)
//                        .run {
//                            subscriptionManager
//                                .getSubscriptions()
//                                .forEach {
//                                    subscribe(it.topic, it.maxQoS)
//                                }
//                        }
            is MqttDisconnectedEvent ->
                updatePersistentNotification(ClientState.Disconnected)
//                is MqttEvent.CommandFailed ->
//                    when (event.command) {
//                        MqttCommand.Connect, MqttCommand.Disconnect ->
//                            updatePersistentNotification(ClientState.Unknown)
//                    }
//                is MqttEvent.MessageReceived -> processMessageReceived(event)
//                is MqttEvent.MessagePublished -> processMessagePublished(event)
            else -> null
        }
    }

    override fun onSubscriptionAdded(subscription: SubscriptionConfiguration) {

    }

    override fun onSubscriptionRemoved(subscription: SubscriptionConfiguration) {
    }
//
//    internal fun connect() {
//        // Disconnect if already connected
//        client?.disconnect()
//
//        client =
//            HeyEddieApplication
//                .applicationComponent
//                .clientComponentBuilder()
//                .clientModule(ClientModule(this))
//                .build()
//                .provideClient()
//                .also { it.connect() }
//    }
//
//    internal fun disconnect() {
//        client?.disconnect()
//    }
//
//    internal fun dismissNotification(notificationId: Int) {
//        launch {
//            notifier.dismiss(notificationId)
//        }
//    }
//
//    internal fun publish(message: Message) {
//        client?.publish(message)
//    }
//
//    internal fun subscribe(topic: String, qos: Int) {
//        client?.subscribe(topic, qos)
//    }
//
//    internal fun unsubscribe(topic: String) {
//        client?.unsubscribe(topic)
//    }

    class StartReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
//                context?.let {
//                    it.startForegroundService(
//                        Intent(
//                            it,
//                            MqttService::class.java
//                        )
//                    )
//                }
            } catch (t: Throwable) {
                Timber.e(t)
            }
        }
    }
}



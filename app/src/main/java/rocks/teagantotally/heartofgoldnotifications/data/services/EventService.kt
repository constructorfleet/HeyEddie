package rocks.teagantotally.heartofgoldnotifications.data.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.SubComponent
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.data.services.helpers.LongRunningServiceConnection
import rocks.teagantotally.heartofgoldnotifications.data.services.helpers.ServiceBinder
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientModule
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientEvent
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.ProcessEventUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class EventService : Service(), Scoped {
    companion object {
        lateinit var serviceBinder: ServiceBinder<EventService>
        val longRunningServiceConnection: LongRunningServiceConnection<EventService> =
            LongRunningServiceConnection()
    }

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext = job.plus(Dispatchers.IO)

    @Inject
    lateinit var channelManager: ChannelManager

    @Inject
    lateinit var eventProcessor: ProcessEventUseCase

    private val eventChannel: ReceiveChannel<ClientEvent> by lazy { channelManager.eventChannel.openSubscription() }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int =
        HeyEddieApplication.clientComponent
            .let {
                when (it) {
                    is SubComponent.Initialized -> it.component
                    is SubComponent.NotInitialized ->
                        HeyEddieApplication.setClient(ClientModule(this))
                }.let { it.inject(this) }
            }
            .run { EventService.serviceBinder = ServiceBinder(this@EventService) }
            .run {
                bindService(
                    Intent(this@EventService, EventService::class.java),
                    EventService.longRunningServiceConnection,
                    Context.BIND_AUTO_CREATE
                )
            }
            .run { listen() }
            .run { START_STICKY }

    override fun onBind(intent: Intent?): IBinder? =
        null

    private fun listen() {
        launch {
            if (!eventChannel.isClosedForReceive) {
                eventChannel.consumeEach {
                    eventProcessor(it)
                }
            }
        }
    }
}
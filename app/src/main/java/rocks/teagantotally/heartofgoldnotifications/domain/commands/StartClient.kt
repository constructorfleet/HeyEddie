package rocks.teagantotally.heartofgoldnotifications.domain.commands

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.ConnectEvent
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import kotlin.coroutines.CoroutineContext

class StartClient(
    private val channelManager: ChannelManager,
    override var job: Job,
    override val coroutineContext: CoroutineContext
) : BaseCommand<ConnectEvent>(), Scoped {
    private val connectChannel: Channel<ConnectEvent> by lazy { channelManager.connectChannel }

    override fun invoke(command: ConnectEvent) {
        launch {
            if (!connectChannel.isClosedForSend) {
                connectChannel.send(ConnectEvent.Connect)
            }
        }
    }
}
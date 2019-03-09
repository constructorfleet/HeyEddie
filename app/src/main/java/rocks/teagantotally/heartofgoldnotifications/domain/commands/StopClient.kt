package rocks.teagantotally.heartofgoldnotifications.domain.commands

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.CommandEvent
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import kotlin.coroutines.CoroutineContext

class StopClient(
    private val channelManager: ChannelManager,
    override var job: Job,
    override val coroutineContext: CoroutineContext
) : BaseCommand<CommandEvent>(), Scoped {
    private val connectChannel: Channel<CommandEvent> by lazy { channelManager.commandChannel }

    override fun invoke(command: CommandEvent) {
        launch {
            if (!connectChannel.isClosedForSend) {
                connectChannel.send(CommandEvent.Disconnect)
            }
        }
    }
}
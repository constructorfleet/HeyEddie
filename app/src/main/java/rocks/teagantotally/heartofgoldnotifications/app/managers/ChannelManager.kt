package rocks.teagantotally.heartofgoldnotifications.app.managers

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.CommandEvent

class ChannelManager {
    val eventChannel = BroadcastChannel<ClientEvent>(Channel.CONFLATED)

    var commandChannel = Channel<CommandEvent>(Channel.CONFLATED)
}
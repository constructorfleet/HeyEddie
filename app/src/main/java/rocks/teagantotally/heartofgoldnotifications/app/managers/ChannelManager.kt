package rocks.teagantotally.heartofgoldnotifications.app.managers

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Event
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.CommandEvent

class ChannelManager {
    val eventChannel = BroadcastChannel<Event>(Channel.CONFLATED)

    var commandChannel = BroadcastChannel<CommandEvent>(Channel.CONFLATED)
}
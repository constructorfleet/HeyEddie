package rocks.teagantotally.heartofgoldnotifications.app.managers

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import rocks.teagantotally.heartofgoldnotifications.domain.models.ClientEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.ConnectEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.MessageEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.UIEvent

class ChannelManager {
    val eventChannel = BroadcastChannel<ClientEvent>(Channel.CONFLATED)

    var messageChannel = BroadcastChannel<MessageEvent>(Channel.CONFLATED)

    val connectChannel = Channel<ConnectEvent>(Channel.UNLIMITED)

    val notifyChannel = Channel<String>(Channel.UNLIMITED)
}
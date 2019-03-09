package rocks.teagantotally.heartofgoldnotifications.app.managers

import kotlinx.coroutines.channels.Channel
import rocks.teagantotally.heartofgoldnotifications.domain.models.ClientEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.MessageEvent

class ChannelManager {
    val eventChannel = Channel<ClientEvent>()

    var messageChannel = Channel<MessageEvent>()
}
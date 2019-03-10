package rocks.teagantotally.heartofgoldnotifications.presentation.status

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.*
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter

class StatusPresenter(
    view: StatusContract.View,
    private val channelManager: ChannelManager
) : StatusContract.Presenter, ScopedPresenter<StatusContract.View, StatusContract.Presenter>(view) {

    companion object {
        const val CONNECTED = "CONNECTED"
        const val DISCONNECTED = "DISCONNECTED"
        const val ERROR = "ERROR %s"
    }

    private val eventChannel: ReceiveChannel<Event> by lazy { channelManager.eventChannel.openSubscription() }
    private val commandChannel: Channel<CommandEvent> by lazy { channelManager.commandChannel }

    override fun onViewCreated() {
        launch {
            view.showLoading(true)
            if (!commandChannel.isClosedForSend) {
                commandChannel.send(CommandEvent.GetStatus)
            }
        }
        launch {
            while (!eventChannel.isClosedForReceive) {
                eventChannel.consumeEach {
                    when (it) {
                        is ClientStatus ->
                            when (it.isConnected) {
                                true -> CONNECTED
                                false -> DISCONNECTED
                            }
                        is ClientConnection.Successful -> CONNECTED
                        is ClientConnection.Failed -> ERROR.format(it.throwable.message)
                        is ClientDisconnection.Successful -> DISCONNECTED
                        is ClientDisconnection.Failed -> ERROR.format(it.throwable.message)
                        is ClientMessageReceive.Successful ->
                            view.logMessage(it.message)
                                .run { null }
                        else -> null
                    }?.let { view.showStatus(it) }
                }
            }
        }
    }

    override fun onDestroyView() {
        // no-op
    }
}
package rocks.teagantotally.heartofgoldnotifications.presentation.status

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.*
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StartClientUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter

class StatusPresenter(
    view: StatusContract.View,
    private val commandChannel: SendChannel<CommandEvent>,
    private val eventChannel: ReceiveChannel<Event>,
    private val startClientUseCase: StartClientUseCase
) : StatusContract.Presenter, ScopedPresenter<StatusContract.View, StatusContract.Presenter>(view) {

    companion object {
        const val CONNECTED = "CONNECTED"
        const val DISCONNECTED = "DISCONNECTED"
        const val ERROR = "ERROR %s"
    }

    override fun onViewCreated() {
        launch {
            view.showLoading(true)
            startClientUseCase(CommandEvent.Connect)
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
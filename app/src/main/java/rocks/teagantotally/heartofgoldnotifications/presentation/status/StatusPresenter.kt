package rocks.teagantotally.heartofgoldnotifications.presentation.status

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.ConnectionCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ConnectionEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MessageEvent
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StartClientUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter

class StatusPresenter(
    view: StatusContract.View,
    private val connectionEventChannel: ReceiveChannel<ConnectionEvent>,
    private val messageEventChannel: ReceiveChannel<MessageEvent>,
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
            startClientUseCase(ConnectionCommand.Connect)
        }
        launch {
            while (!connectionEventChannel.isClosedForReceive) {
                connectionEventChannel.consumeEach {
                    when (it.isConnected) {
                        true -> CONNECTED
                        false -> DISCONNECTED
                    }.let { view.showStatus(it) }
                }
            }
        }
        launch {
            while (!messageEventChannel.isClosedForReceive) {
                messageEventChannel.consumeEach {
                    view.logMessage(it.message)
                }
            }
        }
    }

    override fun onDestroyView() {
        // no-op
    }
}
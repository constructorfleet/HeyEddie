package rocks.teagantotally.heartofgoldnotifications.presentation.main

import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientContainer
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientConfigurationChangedEvent
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.config.ClientConfigurationSavedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.connection.ConnectClient
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.connection.DisconnectClient
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ConnectionViewState
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter
import rocks.teagantotally.kotqtt.domain.framework.client.CommandResult
import rocks.teagantotally.kotqtt.domain.framework.client.MqttCommandExecutor
import rocks.teagantotally.kotqtt.domain.models.commands.MqttGetStatusCommand
import rocks.teagantotally.kotqtt.domain.models.events.MqttConnectedEvent
import rocks.teagantotally.kotqtt.domain.models.events.MqttDisconnectedEvent
import rocks.teagantotally.kotqtt.domain.models.events.MqttEvent
import rocks.teagantotally.kotqtt.domain.models.events.MqttStatusEvent

class MainActivityPresenter(
    view: MainActivityContract.View,
    private val clientConfigurationChangedUseCase: ClientConfigurationSavedUseCase,
    private val configManager: ConnectionConfigManager,
    coroutineScope: CoroutineScope
) : MainActivityContract.Presenter,
    ScopedPresenter<MainActivityContract.View, MainActivityContract.Presenter>(view, coroutineScope) {
    private lateinit var connectionViewState: ConnectionViewState

    private val clientContainer: ClientContainer?
        get() = HeyEddieApplication.clientComponent?.provideClientContainer()

    private val connectClient: ConnectClient?
        get() = clientContainer?.connectClient
    private val disconnectClient: DisconnectClient?
        get() = clientContainer?.disconnectClient
    private val eventReceiver: ReceiveChannel<MqttEvent>?
        get() = clientContainer?.eventProducer?.subscribe()
    private val commandExecutor: MqttCommandExecutor?
        get() = clientContainer?.commandExecutor

    private var isListening: Boolean = false

    override fun onViewCreated() {
        configManager.getConnectionConfiguration()
            ?.let { config ->
                view.showLoading()
                listenForEvents()
                launch {
                    commandExecutor?.execute(MqttGetStatusCommand)
                }
                view.showHistory()
            } ?: ConnectionViewState.Unconfigured
            .let {
                connectionViewState = it
                view.setConnectionState(it)
                view.showConfigSettings()
            }
    }

    override fun onHandleConnectionNavigation() {
        launch {
            view.showLoading(true)
            when (connectionViewState) {
                ConnectionViewState.Connected -> disconnect()
                ConnectionViewState.Disconnected -> connect()
                ConnectionViewState.Checking -> return@launch
                ConnectionViewState.Unconfigured ->
                    view.showConfigSettings()
                        .run { view.showLoading(false) }
            }
        }
    }

    override fun onNavigateToConfigSettings() {
        view.showConfigSettings()
    }

    override fun onNavigateToSubscriptions() {
        view.showSubscriptions()
    }

    override fun onNavigateToHistory() {
        view.showHistory()
    }

    override fun onNavigateToPublish() {
        view.showPublish()
    }

    private fun disconnect() {
        launch {
            view.showLoading(true)
            disconnectClient
                ?.invoke()
                ?.run { view.setConnectionState(ConnectionViewState.Disconnecting) }
        }
    }

    private fun connect() {
        launch {
            view.showLoading(true)
            connectClient?.invoke()
                ?.run { view.setConnectionState(ConnectionViewState.Connecting) }
        }
    }

    override fun onDestroyView() {
        // no-op
    }

    private fun listenForEvents() {
        launch {
            while (eventReceiver?.isClosedForReceive == false) {
                eventReceiver?.receiveOrNull()
                    ?.let { consume(it) }
                    ?: run { isListening = false }
            }
        }
    }

    private fun consume(receivedEvent: MqttEvent) {
        Timber.d { "Received ${receivedEvent}" }
        val event: MqttEvent =
            (receivedEvent as? CommandResult.Success<*, *>)
                ?.let {
                    when (it.result) {
                        is MqttConnectedEvent -> it.result as? MqttConnectedEvent
                        is MqttDisconnectedEvent -> it.result as? MqttDisconnectedEvent
                        else -> null
                    }
                }
                ?: receivedEvent

        when (event) {
            is MqttConnectedEvent -> ConnectionViewState.Connected
            is MqttDisconnectedEvent -> ConnectionViewState.Disconnected
            is MqttStatusEvent ->
                when (event.isConnected) {
                    true -> ConnectionViewState.Connected
                    false -> ConnectionViewState.Disconnected
                }
            else -> null
        }
            ?.also { connectionViewState = it }
            ?.also { view.setConnectionState(it) }
            ?.also { view.showLoading(false) }
    }
}
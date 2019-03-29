package rocks.teagantotally.heartofgoldnotifications.presentation.main

import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientContainer
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.ClientConfigurationChangedEvent
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.config.ClientConfigurationChangedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.connection.ConnectClient
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.connection.DisconnectClient
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.connection.GetClientStatus
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ConnectionViewState
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter
import rocks.teagantotally.kotqtt.domain.framework.client.CommandResult
import rocks.teagantotally.kotqtt.domain.framework.client.MqttCommandExecutor
import rocks.teagantotally.kotqtt.domain.models.commands.MqttConnectCommand
import rocks.teagantotally.kotqtt.domain.models.commands.MqttDisconnectCommand
import rocks.teagantotally.kotqtt.domain.models.commands.MqttGetStatusCommand
import rocks.teagantotally.kotqtt.domain.models.events.MqttConnectedEvent
import rocks.teagantotally.kotqtt.domain.models.events.MqttDisconnectedEvent
import rocks.teagantotally.kotqtt.domain.models.events.MqttEvent
import rocks.teagantotally.kotqtt.domain.models.events.MqttStatusEvent

class MainActivityPresenter(
    view: MainActivityContract.View,
    private val clientConfigurationChangedUseCase: ClientConfigurationChangedUseCase,
    private val configManager: ConnectionConfigManager,
    coroutineScope: CoroutineScope
) : MainActivityContract.Presenter,
    ScopedPresenter<MainActivityContract.View, MainActivityContract.Presenter>(view, coroutineScope) {
    private lateinit var connectionViewState: ConnectionViewState

    private val clientContainer: ClientContainer by lazy { HeyEddieApplication.clientComponent.provideClientContainer() }

    private val connectClient: ConnectClient by lazy { clientContainer.connectClient }
    private val disconnectClient: DisconnectClient by lazy { clientContainer.disconnectClient }
    private val getClientStatus: GetClientStatus by lazy { clientContainer.getClientStatus }
    private val eventReceiver: ReceiveChannel<MqttEvent> by lazy { clientContainer.eventProducer.subscribe() }
    private val commandExecutor: MqttCommandExecutor by lazy { clientContainer.commandExecutor }

    private val clientConfigurationChanged: ReceiveChannel<ClientConfigurationChangedEvent> by lazy { clientConfigurationChangedUseCase.openSubscription() }
    private var isListening: Boolean = false

    override fun onViewCreated() {
        configManager.getConnectionConfiguration()
            ?.let { config ->
                view.showLoading()
                listenForEvents()
                launch {
                    commandExecutor.execute(MqttGetStatusCommand)
                }
            } ?: ConnectionViewState.Unconfigured
            .let {
                connectionViewState = it
                view.setConnectionState(it)
                view.showConfigSettings()
                listenForConfigurationChange()
            }
    }

    override fun onHandleConnectionNavigation() {
        launch {
            view.showLoading(true)
            when (connectionViewState) {
                ConnectionViewState.Connected -> disconnectClient()
                ConnectionViewState.Disconnected -> connectClient()
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

    override fun onDestroyView() {
        // no-op
    }

    private fun listenForConfigurationChange() {
        launch {
            clientConfigurationChanged.receiveOrNull()?.let {
                listenForEvents()
            }
        }
    }

    private fun listenForEvents() {
        if (isListening) {
            return
        }
        isListening = true
        launch {
            while (!eventReceiver.isClosedForReceive) {
                eventReceiver.receiveOrNull()?.let { consume(it) }
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
            is CommandResult.Failure<*> ->
                when (event.command) {
                    is MqttConnectCommand, MqttDisconnectCommand ->
                        ConnectionViewState.Unconfigured
                    else -> null
                }
            else -> null
        }
            ?.also { connectionViewState = it }
            ?.let { view.setConnectionState(it) }
            ?.run { view.showLoading(false) }
    }
}
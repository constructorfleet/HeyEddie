package rocks.teagantotally.heartofgoldnotifications.presentation.main

import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientContainer
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.ConnectClient
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.DisconnectClient
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ConnectionViewState
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter
import rocks.teagantotally.kotqtt.domain.framework.client.CommandResult
import rocks.teagantotally.kotqtt.domain.framework.client.MqttCommandExecutor
import rocks.teagantotally.kotqtt.domain.framework.client.MqttEventProducer
import rocks.teagantotally.kotqtt.domain.models.commands.MqttConnectCommand
import rocks.teagantotally.kotqtt.domain.models.commands.MqttDisconnectCommand
import rocks.teagantotally.kotqtt.domain.models.commands.MqttGetStatusCommand
import rocks.teagantotally.kotqtt.domain.models.events.MqttConnectedEvent
import rocks.teagantotally.kotqtt.domain.models.events.MqttDisconnectedEvent
import rocks.teagantotally.kotqtt.domain.models.events.MqttEvent
import rocks.teagantotally.kotqtt.domain.models.events.MqttStatusEvent

class MainActivityPresenter(
    view: MainActivityContract.View,
    private val configManager: ConnectionConfigManager
) : MainActivityContract.Presenter, ScopedPresenter<MainActivityContract.View, MainActivityContract.Presenter>(view) {
    private lateinit var connectionViewState: ConnectionViewState

    private val clientContainer: ClientContainer by lazy { HeyEddieApplication.clientComponent.provideClientContainer() }

    private val connectClient: ConnectClient by lazy { clientContainer.connectClient }
    private val disconnectClient: DisconnectClient by lazy { clientContainer.disconnectClient }
    private val eventProducer: MqttEventProducer by lazy { clientContainer.eventProducer }
    private val commandExecutor: MqttCommandExecutor by lazy { clientContainer.commandExecutor }

    override fun onViewCreated() {
        configManager.getConnectionConfiguration()
            ?.let { config ->
                view.showLoading()
                launch {
                    eventProducer.subscribe().consumeEach { consume(it) }
                    commandExecutor.execute(MqttGetStatusCommand)
                }
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

    private fun consume(receivedEvent: MqttEvent) {
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
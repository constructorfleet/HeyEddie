package rocks.teagantotally.heartofgoldnotifications.presentation.main

import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrue
import rocks.teagantotally.heartofgoldnotifications.domain.framework.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MqttEvent
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StartClientUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StopClientUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ConnectionViewState
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter

class MainActivityPresenter(
    view: MainActivityContract.View,
    private val configProvider: ConnectionConfigProvider,
    private val startClient: StartClientUseCase,
    private val stopClient: StopClientUseCase
) : MainActivityContract.Presenter, ScopedPresenter<MainActivityContract.View, MainActivityContract.Presenter>(view) {
    private var connectionViewState: ConnectionViewState = ConnectionViewState.Unconfigured

    override fun onHandleConnectionNavigation() {
        launch {
            view.showLoading(true)
            when (connectionViewState) {
                ConnectionViewState.Connected -> stopClient(MqttCommand.Disconnect)
                ConnectionViewState.Disconnected -> startClient(MqttCommand.Connect)
                ConnectionViewState.Unconfigured ->
                    view.showConfigSettings()
                        .run { view.showLoading(false) }
            }
        }
    }

    override fun onNavigateToConfigSettings() {
        view.showConfigSettings()
    }

    override fun onViewCreated() {
        launch {
            when (configProvider.getConnectionConfiguration()?.autoReconnect) {
                null -> view.setConnectionState(ConnectionViewState.Unconfigured)
                true ->
                    view.showLoading(true)
                        .run { startClient(MqttCommand.Connect) }
                false -> view.setConnectionState(ConnectionViewState.Disconnected)
            }
        }
        if (configProvider.hasConnectionConfiguration()) {
            view.showStatus()
        } else {
            view.showConfigSettings()
        }
    }

    override fun onDestroyView() {
        // no-op
    }

    override fun consume(event: MqttEvent) {
        when (event) {
            is MqttEvent.Connected -> ConnectionViewState.Connected
            is MqttEvent.Disconnected -> ConnectionViewState.Disconnected
            is MqttEvent.CommandFailed ->
                when (event.command) {
                    MqttCommand.Connect, MqttCommand.Disconnect ->
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
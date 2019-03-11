package rocks.teagantotally.heartofgoldnotifications.presentation.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.common.extensions.safeLet
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.models.ConnectionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.CommandEvent
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StartClientUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter

class ConfigPresenter(
    view: ConfigContract.View,
    private val connectionConfigProvider: ConnectionConfigProvider,
    private val startClientUseCase: StartClientUseCase,
    coroutineScope: CoroutineScope
) : ScopedPresenter<ConfigContract.View, ConfigContract.Presenter>(view, coroutineScope), ConfigContract.Presenter {
    override fun saveConfig(
        host: String,
        port: Int,
        username: String?,
        password: String?,
        clientId: String,
        reconnect: Boolean,
        cleanSession: Boolean
    ) {
        connectionConfigProvider.setConnectionConfiguration(
            ConnectionConfiguration(
                host,
                port,
                username,
                password,
                clientId,
                reconnect,
                cleanSession
            )
        )
            .run { launch { startClientUseCase(CommandEvent.Connect) } }
            .run { view.close() }
    }

    override fun onViewCreated() {
        if (!connectionConfigProvider.hasConnectionConfiguration()) {
            return
        }

        connectionConfigProvider.getConnectionConfiguration()
            .let {
                view.setHost(it.brokerHost)
                view.setPort(it.brokerPort)
                view.setClientId(it.clientId)
                view.setUsername(it.clientUsername)
                view.setPassword(it.clientPassword)
                view.setReconnect(it.autoReconnect)
                view.setCleanSession(it.cleanSession)

                checkValidity(
                    it.brokerHost,
                    it.brokerPort,
                    it.clientUsername,
                    it.clientPassword,
                    it.clientId,
                    it.autoReconnect,
                    it.cleanSession
                )
            }
    }

    override fun checkValidity(
        host: String?,
        port: Int?,
        username: String?,
        password: String?,
        clientId: String?,
        reconnect: Boolean?,
        cleanSession: Boolean?
    ) {
        safeLet(host, port, clientId) { host, _, clientId ->
            if (host.isBlank() || clientId.isBlank()) {
                null
            } else {
                view.isValid = true
            }
        } ?: run { view.isValid = false }
    }

    override fun onDestroyView() {
        // no-op
    }
}
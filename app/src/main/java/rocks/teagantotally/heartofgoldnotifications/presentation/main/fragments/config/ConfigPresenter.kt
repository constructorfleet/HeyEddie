package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.common.extensions.safeLet
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter

class ConfigPresenter(
    view: ConfigContract.View,
    private val connectionConfigManager: ConnectionConfigManager,
    coroutineScope: CoroutineScope
) : ScopedPresenter<ConfigContract.View, ConfigContract.Presenter>(view, coroutineScope), ConfigContract.Presenter {
    override fun saveConfig(
        host: String,
        port: Int,
        username: String?,
        password: String?,
        clientId: String,
        reconnect: Boolean,
        cleanSession: Boolean,
        notificationAutoCancelMinutes: Int?
    ) {
        view.showLoading(true)
            .run {
                launch {
                    connectionConfigManager.setConnectionConfiguration(
                        ConnectionConfiguration(
                            host,
                            port,
                            username,
                            password,
                            clientId,
                            reconnect,
                            cleanSession,
                            notificationAutoCancelMinutes ?: ConnectionConfiguration.DEFAULT_AUTO_CANCEL_MINUTES
                        )
                    )
                    view.showLoading(false)
                    view.close()
                }
            }
    }

    override fun onViewCreated() {
        if (!connectionConfigManager.hasConnectionConfiguration()) {
            return
        }

        connectionConfigManager.getConnectionConfiguration()
            ?.let {
                view.setHost(it.brokerHost)
                view.setPort(it.brokerPort)
                view.setClientId(it.clientId)
                view.setUsername(it.clientUsername)
                view.setPassword(it.clientPassword)
                view.setReconnect(it.autoReconnect)
                view.setCleanSession(it.cleanSession)
                view.setNotificationAutoCancel(it.notificationCancelMinutes)

                checkValidity(
                    it.brokerHost,
                    it.brokerPort,
                    it.clientUsername,
                    it.clientPassword,
                    it.clientId,
                    it.autoReconnect,
                    it.cleanSession,
                    it.notificationCancelMinutes
                )
            }
            ?: run { view.isValid = false }
    }

    override fun checkValidity(
        host: String?,
        port: Int?,
        username: String?,
        password: String?,
        clientId: String?,
        reconnect: Boolean?,
        cleanSession: Boolean?,
        notificationAutoCancelMinutes: Int?
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
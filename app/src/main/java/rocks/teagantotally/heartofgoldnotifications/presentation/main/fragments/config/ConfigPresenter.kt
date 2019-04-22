package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.common.extensions.safeLet
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.NotificationConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.NotificationConfiguration
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter
import rocks.teagantotally.kotqtt.domain.models.Message

class ConfigPresenter(
    view: ConfigContract.View,
    private val connectionConfigManager: ConnectionConfigManager,
    private val notificationConfigManager: NotificationConfigManager,
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
        lastWill: Message?,
        autoDismiss: Int?,
        debug: Boolean?
    ) {
        view.showLoading(true)
            .run {
                launch {
                    connectionConfigManager.saveConfiguration(
                        ConnectionConfiguration(
                            host,
                            port,
                            username,
                            password,
                            clientId,
                            reconnect,
                            cleanSession,
                            lastWill
                        )
                    )
                    notificationConfigManager.saveConfiguration(
                        NotificationConfiguration(
                            autoDismiss ?: NotificationConfiguration.DEFAULT_AUTO_CANCEL_MINUTES,
                            debug ?: false
                        )
                    )
                    view.showLoading(false)
                    view.close()
                }
            }
    }

    override fun onViewCreated() {
        if (!connectionConfigManager.hasConfiguration()) {
            return
        }

        notificationConfigManager.getConfiguration()
            ?.let {
                view.setAutoDismiss(it.notificationCancelMinutes)
                view.setDebug(it.debug)
            }

        connectionConfigManager.getConfiguration()
            ?.let {
                view.setHost(it.brokerHost)
                view.setPort(it.brokerPort)
                view.setClientId(it.clientId)
                view.setUsername(it.clientUsername)
                view.setPassword(it.clientPassword)
                view.setReconnect(it.autoReconnect)
                view.setCleanSession(it.cleanSession)
                view.setLastWill(it.lastWill)

                checkValidity(
                    it.brokerHost,
                    it.brokerPort,
                    it.clientUsername,
                    it.clientPassword,
                    it.clientId,
                    it.autoReconnect,
                    it.cleanSession,
                    it.lastWill
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
        lastWill: Message?
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
package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config

import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView
import rocks.teagantotally.kotqtt.domain.models.Message

interface ConfigContract {
    interface View : BaseView<Presenter> {
        var isValid: Boolean

        fun setHost(host: String)
        fun setPort(port: Int)
        fun setUsername(usernmae: String?)
        fun setPassword(password: String?)
        fun setClientId(clientId: String)
        fun setReconnect(reconnect: Boolean)
        fun setCleanSession(cleanSession: Boolean)
        fun setLastWill(message: Message?)
        fun setAutoDismiss(minutes: Int?)
        fun setDebug(enableDebug: Boolean)

        fun close()
    }

    interface Presenter : BasePresenter {
        fun saveConfig(
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
        )

        fun checkValidity(
            host: String?,
            port: Int?,
            username: String?,
            password: String?,
            clientId: String?,
            reconnect: Boolean?,
            cleanSession: Boolean?,
            lastWill: Message?
        )
    }
}
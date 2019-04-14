package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config

import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView

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

        fun setNotificationAutoCancel(minutes: Int)
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
            notificationAutoCancelMinutes: Int?
        )

        fun checkValidity(
            host: String?,
            port: Int?,
            username: String?,
            password: String?,
            clientId: String?,
            reconnect: Boolean?,
            cleanSession: Boolean?,
            notificationAutoCancelMinutes: Int?
        )
    }
}
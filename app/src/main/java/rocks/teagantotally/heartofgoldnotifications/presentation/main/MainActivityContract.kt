package rocks.teagantotally.heartofgoldnotifications.presentation.main

import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.MqttEventConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MqttEvent
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ConnectionViewState

interface MainActivityContract {
    interface View : BaseView<Presenter> {
        fun showConfigSettings()

        fun showHistory()

        fun showSubscriptions()

        fun setConnectionState(state: ConnectionViewState)
    }

    interface Presenter : BasePresenter, MqttEventConsumer {
        fun onHandleConnectionNavigation()

        fun onNavigateToConfigSettings()

        fun onNavigateToSubscriptions()
    }
}
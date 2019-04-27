package rocks.teagantotally.heartofgoldnotifications.presentation.main

import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ConnectionViewState

interface MainActivityContract {
    interface View : BaseView<Presenter> {
        fun showConfigSettings(screenKey: String? = null)

        fun showHistory()

        fun showSubscriptions()

        fun showPublish()

        fun setConnectionState(state: ConnectionViewState)
    }

    interface Presenter : BasePresenter {
        fun onHandleConnectionNavigation()

        fun onNavigateToConfigSettings()

        fun onNavigateToSubscriptions()

        fun onNavigateToHistory()

        fun onNavigateToPublish()
    }
}
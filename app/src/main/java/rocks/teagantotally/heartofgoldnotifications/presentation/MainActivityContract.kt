package rocks.teagantotally.heartofgoldnotifications.presentation

import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView

interface MainActivityContract {
    interface View : BaseView<Presenter> {
        fun showConfigSettings()

        fun showStatus()
    }

    interface Presenter : BasePresenter {
        fun onNavigateToConfigSettings()
    }
}
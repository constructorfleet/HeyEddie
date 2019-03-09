package rocks.teagantotally.heartofgoldnotifications.presentation.status

import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView

interface StatusContract {
    interface View : BaseView<Presenter> {
        fun showNeedsConfigured()

        fun showConnected(connected: Boolean)

        fun showConfigured(configured: Boolean)
    }

    interface Presenter: BasePresenter {
        fun checkConnectionConfiguration()
    }
}
package rocks.teagantotally.heartofgoldnotifications.presentation.pubish

import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView

interface PublishContract {
    interface View: BaseView<Presenter> {
        fun showSuccess()
    }

    interface Presenter : BasePresenter {
        fun checkValid()

        fun publish()
    }
}
package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.pubish

import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView

interface PublishContract {
    interface View : BaseView<Presenter> {
        var isValid: Boolean

        fun showSuccess()
    }

    interface Presenter : BasePresenter {
        fun checkValid(
            topic: String?,
            payload: String?
        )

        fun publish(
            topic: String,
            payload: String,
            retain: Boolean,
            qos: Int
        )
    }
}
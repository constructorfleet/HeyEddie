package rocks.teagantotally.heartofgoldnotifications.presentation.history

import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView

interface HistoryContract {
    interface View : BaseView<Presenter> {
        fun showStatus(clientStatus: String)

        fun logMessage(message: Message)
    }

    interface Presenter : BasePresenter
}
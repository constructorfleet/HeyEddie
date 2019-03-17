package rocks.teagantotally.heartofgoldnotifications.presentation.history

import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView

interface HistoryContract {
    interface View : BaseView<Presenter> {
        fun logMessageReceived(message: Message)

        fun logMessagePublished(message: Message)

        fun clearHistory()
    }

    interface Presenter : BasePresenter {
        fun onDeleteHistory()
    }
}
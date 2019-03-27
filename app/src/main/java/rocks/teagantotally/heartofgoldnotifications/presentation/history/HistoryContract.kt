package rocks.teagantotally.heartofgoldnotifications.presentation.history

import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView
import rocks.teagantotally.kotqtt.domain.models.Message

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
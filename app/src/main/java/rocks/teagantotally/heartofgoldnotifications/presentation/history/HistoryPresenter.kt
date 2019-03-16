package rocks.teagantotally.heartofgoldnotifications.presentation.history

import rocks.teagantotally.heartofgoldnotifications.domain.framework.MessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter

class HistoryPresenter(
    view: HistoryContract.View,
    val messageHistoryManager: MessageHistoryManager
) : HistoryContract.Presenter, ScopedPresenter<HistoryContract.View, HistoryContract.Presenter>(view) {

    override fun onViewCreated() {
        messageHistoryManager
            .getReceivedMessages()
            .forEach { view.logMessageReceived(it) }
        messageHistoryManager
            .getPublishedMessages()
            .forEach { view.logMessagePublished(it) }
    }

    override fun onDestroyView() {
        // no-op
    }
}
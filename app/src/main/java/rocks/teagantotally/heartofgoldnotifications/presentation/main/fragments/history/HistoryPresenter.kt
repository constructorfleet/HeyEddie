package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.history

import kotlinx.coroutines.CoroutineScope
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.MessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter
import rocks.teagantotally.kotqtt.domain.models.Message

class HistoryPresenter(
    view: HistoryContract.View,
    private val messageHistoryManager: MessageHistoryManager,
    coroutineScope: CoroutineScope
) : HistoryContract.Presenter, ScopedPresenter<HistoryContract.View, HistoryContract.Presenter>(view, coroutineScope),
    MessageHistoryManager.Listener {

    override fun onViewCreated() {
        messageHistoryManager.addListener(this)
        messageHistoryManager
            .getReceivedMessages()
            .forEach { view.logMessageReceived(it) }
        messageHistoryManager
            .getPublishedMessages()
            .forEach { view.logMessagePublished(it) }
    }

    override fun onDestroyView() {
        messageHistoryManager.removeListener(this)
    }

    override fun onMessageReceived(message: Message) {
        view.logMessageReceived(message)
    }

    override fun onMessagePublished(message: Message) {
        view.logMessagePublished(message)
    }

    override fun onDeleteHistory() {
        messageHistoryManager.clear()
        view.clearHistory()
    }
}
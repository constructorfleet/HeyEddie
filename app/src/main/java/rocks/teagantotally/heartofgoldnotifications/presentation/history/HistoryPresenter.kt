package rocks.teagantotally.heartofgoldnotifications.presentation.history

import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.domain.framework.MessageHistoryManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StartClientUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter

class HistoryPresenter(
    view: HistoryContract.View,
    val messageHistoryManager: MessageHistoryManager
) : HistoryContract.Presenter, ScopedPresenter<HistoryContract.View, HistoryContract.Presenter>(view) {

    override fun onViewCreated() {

    }

    override fun onDestroyView() {
        // no-op
    }
}
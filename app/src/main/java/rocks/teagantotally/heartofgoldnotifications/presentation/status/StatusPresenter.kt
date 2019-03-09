package rocks.teagantotally.heartofgoldnotifications.presentation.status

import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter

class StatusPresenter(
    view: StatusContract.View,
    private val connectionConfigProvider: ConnectionConfigProvider
) : StatusContract.Presenter, ScopedPresenter<StatusContract.View, StatusContract.Presenter>(view) {
    override fun onViewCreated() {
        checkConnectionConfiguration()
    }

    override fun onDestroyView() {
        // no-op
    }

    override fun checkConnectionConfiguration() {
        view.showConfigured(connectionConfigProvider.hasConnectionConfiguration())
    }
}
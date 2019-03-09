package rocks.teagantotally.heartofgoldnotifications.presentation

import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter

class MainActivityPresenter(
    view: MainActivityContract.View,
    val configProvider: ConnectionConfigProvider
) : MainActivityContract.Presenter, ScopedPresenter<MainActivityContract.View, MainActivityContract.Presenter>(view) {
    override fun onNavigateToConfigSettings() {
        view.showConfigSettings()
    }

    override fun onViewCreated() {
        if (configProvider.hasConnectionConfiguration()) {
            view.showStatus()
        } else {
            view.showConfigSettings()
        }
    }

    override fun onDestroyView() {
        // no-op
    }
}
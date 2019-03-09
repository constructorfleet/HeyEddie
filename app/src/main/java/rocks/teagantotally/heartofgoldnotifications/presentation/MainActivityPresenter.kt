package rocks.teagantotally.heartofgoldnotifications.presentation

import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter

class MainActivityPresenter(
    view: MainActivityContract.View
) : MainActivityContract.Presenter, ScopedPresenter<MainActivityContract.View, MainActivityContract.Presenter>(view) {
    override fun onNavigateToConfigSettings() {
        view.showConfigSettings()
    }

    override fun onViewCreated() {

    }

    override fun onDestroyView() {

    }
}
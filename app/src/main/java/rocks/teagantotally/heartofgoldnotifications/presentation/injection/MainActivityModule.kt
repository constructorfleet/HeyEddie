package rocks.teagantotally.heartofgoldnotifications.presentation.injection

import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.ActivityScope
import rocks.teagantotally.heartofgoldnotifications.domain.framework.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.presentation.MainActivityContract
import rocks.teagantotally.heartofgoldnotifications.presentation.MainActivityPresenter

@Module
class MainActivityModule(
    private val view: MainActivityContract.View
) {
    @Provides
    @ActivityScope
    fun provideView(): MainActivityContract.View =
        view

    @Provides
    @ActivityScope
    fun providePresenter(
        view: MainActivityContract.View,
        configProvider: ConnectionConfigProvider
    ): MainActivityContract.Presenter =
        MainActivityPresenter(
            view,
            configProvider
        )
}
package rocks.teagantotally.heartofgoldnotifications.presentation.main.injection

import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.ActivityScope
import rocks.teagantotally.heartofgoldnotifications.domain.framework.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StartClientUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StopClientUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivityContract
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivityPresenter

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
        configProvider: ConnectionConfigProvider,
        startClientUseCase: StartClientUseCase,
        stopClientUseCase: StopClientUseCase
    ): MainActivityContract.Presenter =
        MainActivityPresenter(
            view,
            configProvider,
            startClientUseCase,
            stopClientUseCase
        )
}
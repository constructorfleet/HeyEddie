package rocks.teagantotally.heartofgoldnotifications.presentation.main.injection

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.UI
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.ActivityScope
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.config.ClientConfigurationChangedUseCase
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
        configurationChanged: ClientConfigurationChangedUseCase,
        configManager: ConnectionConfigManager,
        @UI coroutineScope: CoroutineScope
    ): MainActivityContract.Presenter =
        MainActivityPresenter(
            view,
            configurationChanged,
            configManager,
            coroutineScope
        )
}
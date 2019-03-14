package rocks.teagantotally.heartofgoldnotifications.presentation.config.injection

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.domain.framework.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.StartClientUseCase
import rocks.teagantotally.heartofgoldnotifications.presentation.config.ConfigContract
import rocks.teagantotally.heartofgoldnotifications.presentation.config.ConfigPresenter

@Module
class ConfigModule(
    private val view: ConfigContract.View
) {
    @Provides
    @FragmentScope
    fun provideView(): ConfigContract.View =
        view

    @Provides
    @FragmentScope
    fun provideCoroutineScope(): CoroutineScope =
        view

    @Provides
    @FragmentScope
    fun providePresenter(
        view: ConfigContract.View,
        connectionConfigProvider: ConnectionConfigProvider,
        startClientUseCase: StartClientUseCase,
        coroutineScope: CoroutineScope
    ): ConfigContract.Presenter =
        ConfigPresenter(
            view,
            connectionConfigProvider,
            startClientUseCase,
            coroutineScope
        )
}
package rocks.teagantotally.heartofgoldnotifications.presentation.config.injection

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.presentation.config.ConfigContract
import rocks.teagantotally.heartofgoldnotifications.presentation.config.ConfigPresenter
import kotlin.coroutines.CoroutineContext

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
    fun providePresenter(
        view: ConfigContract.View,
        connectionConfigProvider: ConnectionConfigProvider,
        coroutineScope: CoroutineScope
    ): ConfigContract.Presenter =
        ConfigPresenter(
            view,
            connectionConfigProvider,
            coroutineScope
        )
}
package rocks.teagantotally.heartofgoldnotifications.presentation.config.injection

import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
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
    fun providePresenter(
        view: ConfigContract.View,
        connectionConfigManager: ConnectionConfigManager
    ): ConfigContract.Presenter =
        ConfigPresenter(
            view,
            connectionConfigManager,
            view
        )
}
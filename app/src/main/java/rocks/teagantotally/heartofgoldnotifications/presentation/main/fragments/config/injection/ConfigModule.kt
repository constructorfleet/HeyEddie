package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.injection

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.UI
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.NotificationConfigManager
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.ConfigContract
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.ConfigPresenter

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
        connectionConfigManager: ConnectionConfigManager,
        notificationConfigManager: NotificationConfigManager,
        @UI coroutineScope: CoroutineScope
    ): ConfigContract.Presenter =
        ConfigPresenter(
            view,
            connectionConfigManager,
            notificationConfigManager,
            coroutineScope
        )
}
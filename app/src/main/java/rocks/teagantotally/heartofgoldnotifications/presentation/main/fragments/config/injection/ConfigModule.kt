package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.injection

import android.support.v7.preference.PreferenceFragmentCompat
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.UI
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.NotificationConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.Configuration
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.ConfigContract
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.ConfigPresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.vm.ConfigViewModel
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.vm.ConnectionConfigViewModel
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.vm.NotificationConfigViewModel

@Module
class ConfigModule(
    private val view: ConfigContract.View,
    private val configScreenKey: String?
) {
    @Provides
    @FragmentScope
    fun provideView(): ConfigContract.View =
        view

    @Provides
    @FragmentScope
    fun provideViewModel(
        connectionConfigManager: ConnectionConfigManager,
        notificationConfigManager: NotificationConfigManager
    ): ConfigViewModel<out Configuration> =
        when (configScreenKey) {
            "connection_settings" -> ConnectionConfigViewModel(
                connectionConfigManager,
                view as PreferenceFragmentCompat
            )
            "notification_settings" -> NotificationConfigViewModel(
                notificationConfigManager,
                view as PreferenceFragmentCompat
            )
            else -> ConfigViewModel(view as PreferenceFragmentCompat)
        }

    @Provides
    @FragmentScope
    fun providePresenter(
        view: ConfigContract.View,
        viewModel: ConfigViewModel<out Configuration>,
        @UI coroutineScope: CoroutineScope
    ): ConfigContract.Presenter =
        ConfigPresenter(
            viewModel,
            view,
            coroutineScope
        )
}
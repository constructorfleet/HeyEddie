package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.common.extensions.safeLet
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.NotificationConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.Configuration
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.NotificationConfiguration
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.vm.ConfigViewModel
import rocks.teagantotally.kotqtt.domain.models.Message

class ConfigPresenter(
    override var viewModel: ConfigViewModel<out Configuration>,
    view: ConfigContract.View,
    coroutineScope: CoroutineScope
) : ScopedPresenter<ConfigContract.View, ConfigContract.Presenter>(view, coroutineScope), ConfigContract.Presenter {

    override fun saveConfig() {
        view.showLoading(true)
            .run {
                launch {
                    viewModel.save()
                    view.showLoading(false)
                    view.close()
                }
            }
    }

    override fun onViewCreated() {
        viewModel.initialize()
        viewModel.populate()
        checkValidity()
    }

    override fun checkValidity() {
        view.isValid = viewModel.isValid()
    }

    override fun onDestroyView() {
        // no-op
    }
}
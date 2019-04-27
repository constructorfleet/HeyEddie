package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config

import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.Configuration
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.vm.ConfigViewModel

interface ConfigContract {
    interface View : BaseView<Presenter> {
        var isValid: Boolean

        fun close()
    }

    interface Presenter : BasePresenter {
        var viewModel: ConfigViewModel<out Configuration>

        fun saveConfig()

        fun checkValidity()
    }
}
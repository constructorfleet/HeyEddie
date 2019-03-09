package rocks.teagantotally.heartofgoldnotifications.presentation.base

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

interface BaseView<PresenterType: BasePresenter>: Scoped {
    var presenter: PresenterType
    fun showLoading(loading: Boolean = true)
    fun showError(message: String?)
}

interface BasePresenter {
    fun onViewCreated()
    fun onDestroyView()
}

abstract class ScopedPresenter<ViewType : BaseView<PresenterType>, PresenterType: BasePresenter>(val view: ViewType, coroutineScope: CoroutineScope = view) : BasePresenter, CoroutineScope by coroutineScope

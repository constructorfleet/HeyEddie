package rocks.teagantotally.heartofgoldnotifications.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import rocks.teagantotally.heartofgoldnotifications.presentation.history.injection.HistoryModule
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivity
import javax.inject.Inject

@ObsoleteCoroutinesApi
class HistoryFragment : BaseFragment(), HistoryContract.View, Scoped {
    @Inject
    override lateinit var presenter: HistoryContract.Presenter

    override val navigationMenuId: Int = R.id.menu_item_history

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_history, container, false)

    @UseExperimental(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainActivity.mainActivityComponent
            .statusComponentBuilder()
            .module(HistoryModule(this))
            .build()
            .inject(this)

        presenter.onViewCreated()
    }

    override fun onResume() {
        super.onResume()
//        received_messages.removeAllViews()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun logMessageReceived(message: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun logMessagePublished(message: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading(loading: Boolean) {
        // no-op
    }

    override fun showError(message: String?) {
        // no-op
    }
}
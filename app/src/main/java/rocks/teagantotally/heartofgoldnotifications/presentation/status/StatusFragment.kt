package rocks.teagantotally.heartofgoldnotifications.presentation.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_status.*
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.presentation.MainActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import rocks.teagantotally.heartofgoldnotifications.presentation.status.injection.StatusModule
import javax.inject.Inject

class StatusFragment : BaseFragment(), StatusContract.View, Scoped {
    @Inject
    override lateinit var presenter: StatusContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_status, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        status.text = "UNKNOWN"

        MainActivity.mainActivityComponent
            .statusComponentBuilder()
            .module(StatusModule(this))
            .build()
            .inject(this)

        presenter.onViewCreated()
    }

    override fun showStatus(clientStatus: String) {
        status.text = clientStatus
    }

    override fun logMessage(message: Message) {
        last_message.text = "${last_message.text}${message.payload}\n"
    }

    override fun showLoading(loading: Boolean) {
        // no-op
    }

    override fun showError(message: String?) {
        // no-op
    }
}
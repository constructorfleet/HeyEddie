package rocks.teagantotally.heartofgoldnotifications.presentation.status

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.ajalt.timberkt.Timber
import kotlinx.android.synthetic.main.fragment_status.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.domain.models.Message
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.*
import rocks.teagantotally.heartofgoldnotifications.presentation.MainActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import rocks.teagantotally.heartofgoldnotifications.presentation.config.injection.ConfigModule
import rocks.teagantotally.heartofgoldnotifications.presentation.status.injection.StatusModule
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class StatusFragment : Fragment(), StatusContract.View, Scoped {
    @Inject
    override lateinit var presenter: StatusContract.Presenter

    override lateinit var job: Job
    override val coroutineContext: CoroutineContext by lazy { job.plus(Dispatchers.Main) }

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

        activity?.startService(
            Intent(
                context,
                MqttService::class.java
            )
        )

        presenter.onViewCreated()
    }

    override fun showStatus(clientStatus: String) {
        status.text = clientStatus
    }

    override fun logMessage(message: Message) {
        last_message.text = "${last_message.text}${String(message.payload)}\n"
    }

    override fun showLoading(loading: Boolean) {
        // no-op
    }

    override fun showError(message: String?) {
        // no-op
    }
}
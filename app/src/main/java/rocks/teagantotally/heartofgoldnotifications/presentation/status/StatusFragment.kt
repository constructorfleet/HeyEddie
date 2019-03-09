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
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.*
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class StatusFragment : Fragment(), Scoped {
    @Inject
    lateinit var connectionConfigProvider: ConnectionConfigProvider

    @Inject
    lateinit var channelManager: ChannelManager

    override lateinit var job: Job
    override val coroutineContext: CoroutineContext by lazy { job.plus(Dispatchers.Main) }
    val eventChannel: ReceiveChannel<ClientEvent> by lazy { channelManager.eventChannel.openSubscription() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_status, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        status.text = "UNCONNECTED"
    }

    override fun onResume() {
        super.onResume()
        HeyEddieApplication.applicationComponent.inject(this)
        if (connectionConfigProvider.hasConnectionConfiguration()) {
            activity?.startService(
                Intent(
                    context,
                    MqttService::class.java
                )
            )
        }
        launch {
            while (!eventChannel.isClosedForReceive) {
                eventChannel.consumeEach {
                    when (it) {
                        is ClientConnection.Successful -> status.text = "CONNECTED"
                        is ClientConnection.Failed, is ClientDisconnection -> status.text = "DISCONNECTED"
                        is ClientMessageReceive.Successful -> last_message.text = "${last_message.text}\n${String(it.message.payload)}"
                        else -> Timber.d { "$it" }
                    }
                }
            }
        }
    }
}
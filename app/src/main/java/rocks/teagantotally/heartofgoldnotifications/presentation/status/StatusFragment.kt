package rocks.teagantotally.heartofgoldnotifications.presentation.status

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_status.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientModule
import rocks.teagantotally.heartofgoldnotifications.domain.models.*
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class StatusFragment : Fragment(), Scoped {
    @Inject
    lateinit var connectionConfigProvider: ConnectionConfigProvider

    @Inject
    lateinit var channelManager: ChannelManager

    override lateinit var job: Job
    override val coroutineContext: CoroutineContext by lazy { job }
    val connectChannel: Channel<ConnectEvent> by lazy { channelManager.connectChannel }
    val eventChannel: ReceiveChannel<ClientEvent> by lazy { channelManager.eventChannel.openSubscription() }
    val notifyChannel: ReceiveChannel<String> by lazy { channelManager.notifyChannel }


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
            connectChannel.send(ConnectEvent.Connect)
        }
        launch {
            while (!eventChannel.isClosedForReceive) {
                eventChannel.receiveOrNull().let {
                    (it as? ClientEvent.Success)
                        ?.let {
                            if (it.type == ClientEventType.Connection) {
                                Handler(Looper.getMainLooper()).post(Runnable {
                                    status.text = "CONNECTED"
                                })
                            }
                        }
                }
            }
        }
        launch {
            while (!notifyChannel.isClosedForReceive) {
                notifyChannel.receiveOrNull()?.let {

                    Handler(Looper.getMainLooper()).post(Runnable {
                        last_message.text = it
                    })
                }
            }
        }
    }
}
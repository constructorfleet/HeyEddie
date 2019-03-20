package rocks.teagantotally.heartofgoldnotifications.data.managers.device

import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.device.DeviceConnectivityEventConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.DeviceEvent
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import kotlin.coroutines.CoroutineContext

class DeviceConnectivityListener(
    private val deviceConnectivityEventConsumer: DeviceConnectivityEventConsumer
) : ConnectivityManager.NetworkCallback(), Scoped {

    override var job: Job = Job()
    override val coroutineContext: CoroutineContext = job.plus(Dispatchers.IO)

    override fun onLost(network: Network?) {
        launch {
            deviceConnectivityEventConsumer.consume(DeviceEvent.NetworkState.Unavailable)
        }
        super.onLost(network)
    }

    override fun onUnavailable() {
        launch {
            deviceConnectivityEventConsumer.consume(DeviceEvent.NetworkState.Unavailable)
        }
        super.onUnavailable()
    }

    override fun onAvailable(network: Network?) {
        super.onAvailable(network)
        launch {
            deviceConnectivityEventConsumer.consume(DeviceEvent.NetworkState.Available)
        }
    }
}
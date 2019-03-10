package rocks.teagantotally.heartofgoldnotifications.data.services.helpers

import android.app.Service
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

class LongRunningServiceConnection<ServiceType : Service> : ServiceConnection {
    var service: ServiceType? = null

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
    }

    @Suppress("UNCHECKED_CAST")
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        (service as? ServiceBinder<ServiceType>)
            ?.let { it.service }
            ?.let { this.service = it }
    }
}
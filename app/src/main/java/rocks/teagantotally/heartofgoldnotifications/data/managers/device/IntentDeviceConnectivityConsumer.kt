package rocks.teagantotally.heartofgoldnotifications.data.managers.device

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrueMaybe
import rocks.teagantotally.heartofgoldnotifications.common.extensions.log
import rocks.teagantotally.heartofgoldnotifications.common.extensions.putInvoker
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.device.DeviceConnectivityEventConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.DeviceEvent

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class IntentDeviceConnectivityConsumer(
    private val context: Context,
    private val connectionConfigManager: ConnectionConfigManager
) : DeviceConnectivityEventConsumer {

    override fun consume(event: DeviceEvent.NetworkState) {
        buildIntent(event)
            ?.also {
                it.log(this::class)
            }
            ?.let { context.sendBroadcast(it) }
    }

    private fun buildIntent(event: DeviceEvent.NetworkState) =
        when (event) {
            DeviceEvent.NetworkState.Available ->
                (connectionConfigManager.hasConnectionConfiguration() && connectionConfigManager.getConnectionConfiguration()?.autoReconnect == true)
                    .ifTrueMaybe { MqttService.ACTION_CONNECT }
            DeviceEvent.NetworkState.Unavailable ->
                MqttService.ACTION_DISCONNECT
        }
            ?.let {
                Intent(it)
                    .putInvoker(IntentDeviceConnectivityConsumer::class)
            }
}
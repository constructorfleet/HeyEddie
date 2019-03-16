package rocks.teagantotally.heartofgoldnotifications.domain.clients.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.SessionScope
import rocks.teagantotally.heartofgoldnotifications.data.common.BrokerUriBuilder
import rocks.teagantotally.heartofgoldnotifications.domain.clients.Client
import rocks.teagantotally.heartofgoldnotifications.domain.clients.MqttClient
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.MqttEventConsumer
import java.lang.IllegalStateException

@Module
class ClientModule(
    private val context: Context
) {
    @Provides
    @SessionScope
    fun provideMqttAsyncClient(
        connectionConfigManager: ConnectionConfigManager
    ): IMqttAsyncClient =
        connectionConfigManager.getConnectionConfiguration()
            ?.let {
                MqttAndroidClient(
                    context,
                    BrokerUriBuilder.getBrokerUri(it),
                    it.clientId
                )
            }
            ?: throw IllegalStateException("Connection is not configured") // TODO : Handle

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    @Provides
    @SessionScope
    fun provideClient(
        mqttAsyncClient: IMqttAsyncClient,
        connectionConfigManager: ConnectionConfigManager,
        mqttEventConsumer: MqttEventConsumer
    ): Client =
        MqttClient(
            mqttAsyncClient,
            connectionConfigManager,
            mqttEventConsumer
        )
}
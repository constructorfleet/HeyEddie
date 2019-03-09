package rocks.teagantotally.heartofgoldnotifications.domain.clients.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.SessionScope
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.data.common.BrokerUriBuilder
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.clients.Client
import rocks.teagantotally.heartofgoldnotifications.domain.clients.MqttClient

@Module
class ClientModule(
    private val context: Context
) {
    @Provides
    @SessionScope
    fun provideMqttAsyncClient(
        connectionConfigProvider: ConnectionConfigProvider
    ): IMqttAsyncClient =
        connectionConfigProvider.getConnectionConfiguration()
            .let {
                MqttAndroidClient(
                    context,
                    BrokerUriBuilder.getBrokerUri(it),
                    it.clientId
                )
            }

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    @Provides
    @SessionScope
    fun provideClient(
        mqttAsyncClient: IMqttAsyncClient,
        channelManager: ChannelManager
    ): Client =
        MqttClient(
            mqttAsyncClient,
            channelManager.eventChannel,
            channelManager.messageChannel,
            channelManager.notifyChannel
        )
}
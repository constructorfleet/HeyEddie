package rocks.teagantotally.heartofgoldnotifications.domain.clients.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.Event
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.IODispatcher
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.Message
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.SessionScope
import rocks.teagantotally.heartofgoldnotifications.data.common.BrokerUriBuilder
import rocks.teagantotally.heartofgoldnotifications.data.common.ConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.data.local.TestConnectionConfigProvider
import rocks.teagantotally.heartofgoldnotifications.domain.clients.Client
import rocks.teagantotally.heartofgoldnotifications.domain.clients.MqttClient
import rocks.teagantotally.heartofgoldnotifications.domain.models.ClientEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.MessageEvent
import kotlin.coroutines.CoroutineContext

@Module
class ClientModule(
    private val context: Context,
    private val eventChannel: Channel<ClientEvent>,
    private val messageChannel: Channel<MessageEvent>
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

    @Provides
    @SessionScope
    @IODispatcher
    fun provideCoroutineContext(): CoroutineContext =
        Dispatchers.IO + Job()

    @ObsoleteCoroutinesApi
    @Provides
    @SessionScope
    fun provideClient(
        mqttAsyncClient: IMqttAsyncClient,
        @IODispatcher coroutineContext: CoroutineContext
    ): Client =
        MqttClient(
            mqttAsyncClient,
            eventChannel,
            messageChannel,
            coroutineContext
        )
}
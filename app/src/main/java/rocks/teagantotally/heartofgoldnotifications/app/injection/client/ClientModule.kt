package rocks.teagantotally.heartofgoldnotifications.app.injection.client

import android.content.Context
import com.github.ajalt.timberkt.Timber
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.IO
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.SessionScope
import rocks.teagantotally.heartofgoldnotifications.common.extensions.safeLet
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import rocks.teagantotally.kotqtt.data.MqttClient
import rocks.teagantotally.kotqtt.domain.framework.client.Client
import rocks.teagantotally.kotqtt.domain.framework.client.MqttCommandExecutor
import rocks.teagantotally.kotqtt.domain.framework.client.MqttEventProducer
import rocks.teagantotally.kotqtt.domain.framework.connections.MqttAuthentication
import rocks.teagantotally.kotqtt.domain.framework.connections.MqttBrokerConnection
import rocks.teagantotally.kotqtt.domain.framework.connections.MqttConnectionOptions
import kotlin.coroutines.CoroutineContext

@Module
class ClientModule(
    private val connectionConfiguration: ConnectionConfiguration
) {

    @Provides
    @SessionScope
    fun provideConnectionConfiguration(): ConnectionConfiguration =
        connectionConfiguration

    @Provides
    @SessionScope
    fun provideBrokerConnection(): MqttBrokerConnection =
        with(connectionConfiguration) {
            MqttBrokerConnection.InsecureMqttBrokerConnection(
                brokerHost,
                brokerPort,
                clientId
            )
        }

    @Provides
    @SessionScope
    fun provideAuthentication(): MqttAuthentication =
        with(connectionConfiguration) {
            safeLet(clientUsername, clientPassword) { username, password ->
                MqttAuthentication.Basic(
                    username,
                    password
                )
            } ?: MqttAuthentication.Unauthenticated
        }

    @Provides
    @SessionScope
    fun provideConnectionOptions(
        authentication: MqttAuthentication
    ): MqttConnectionOptions =
        with(connectionConfiguration) {
            MqttConnectionOptions(
                authentication,
                200, // TODO
                cleanSession,
                autoReconnect
            ) // TODO : Mqtt Version, LastWill
        }

    @Provides
    @SessionScope
    fun provideMqttAsyncClient(
        context: Context,
        brokerConnection: MqttBrokerConnection
    ): IMqttAsyncClient =
        MqttAndroidClient(
            context,
            brokerConnection.brokerUri,
            brokerConnection.clientId
        )

    @Provides
    @SessionScope
    fun provideCoroutineScope(): CoroutineScope =
        object : CoroutineScope {
            val job: Job = Job()
            override val coroutineContext: CoroutineContext by lazy { job.plus(Dispatchers.IO) }
        }

    @Provides
    @SessionScope
    fun provideClient(
        mqttAsyncClient: IMqttAsyncClient,
        connectionOptions: MqttConnectionOptions,
        coroutineScope: CoroutineScope
    ): Client =
        MqttClient(
            mqttAsyncClient,
            connectionOptions,
            coroutineScope
        )

    @Provides
    @SessionScope
    fun provideCommandExecutor(
        client: Client
    ): MqttCommandExecutor =
        client

    @Provides
    @SessionScope
    fun provideEventProducer(
        client: Client,
        @IO coroutineScope: CoroutineScope
    ): MqttEventProducer =
        client
            .also {
                it.subscribe()
                    .also {
                        coroutineScope.launch {
                            while (!it.isClosedForReceive) {
                                it.receiveOrNull()
                                    ?.let { Timber.d { "Received ${it}" } }
                            }
                        }
                    }
            }
}
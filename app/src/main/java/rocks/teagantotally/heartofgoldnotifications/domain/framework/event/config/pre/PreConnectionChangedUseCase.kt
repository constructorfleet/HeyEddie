package rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.pre

import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientContainer
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifFalse
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrue
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrueAlso
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.ClientConfigurationChangedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientConfigurationChangedEvent
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.connection.DisconnectClient
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.connection.GetClientStatus
import rocks.teagantotally.kotqtt.domain.framework.client.CommandResult
import rocks.teagantotally.kotqtt.domain.models.commands.MqttDisconnectCommand
import rocks.teagantotally.kotqtt.domain.models.events.MqttEvent
import rocks.teagantotally.kotqtt.domain.models.events.MqttStatusEvent
import javax.inject.Inject

class PreConnectionChangedUseCase(
    coroutineScope: CoroutineScope
) : ClientConfigurationChangedUseCase,
    PreConnectionConfigurationChangedUseCase, CoroutineScope by coroutineScope {
    @Inject
    lateinit var clientContainer: ClientContainer

    private val disconnectClient: DisconnectClient
        get() = clientContainer.disconnectClient
    private val getClientStatus: GetClientStatus
        get() = clientContainer.getClientStatus
    private val eventReceiver: ReceiveChannel<MqttEvent>
        get() = clientContainer.eventProducer.subscribe()

    override suspend fun invoke(parameter: ClientConfigurationChangedEvent) {
        HeyEddieApplication
            .clientComponent
            ?.inject(this)
        parameter
            .ifTrue({ it.old != null }) {
                disconnectCurrentClient()
            }
    }

    private suspend fun disconnectCurrentClient() {
        var connected = false
        getClientStatus()
        while (!eventReceiver.isClosedForReceive && connected) {
            eventReceiver.receiveOrNull()
                ?.let {
                    when (it) {
                        is MqttStatusEvent ->
                            it.isConnected
                                .ifTrueAlso {
                                    disconnectClient()
                                }
                                .ifFalse { connected = false }
                        is CommandResult<*> ->
                            it.ifTrue({ it.command is MqttDisconnectCommand }) {
                                when (it) {
                                    is CommandResult.Success<*, *> ->
                                        connected = false
                                    is CommandResult.Failure<*> ->
                                        // TODO : Check why
                                        Timber.e { "Unable to disconnect" }
                                }
                            }
                    }
                }
        }
    }
}
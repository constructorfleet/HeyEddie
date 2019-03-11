package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.SubComponent
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrue
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientModule
import rocks.teagantotally.heartofgoldnotifications.domain.framework.EventProcessingUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseResult
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.CommandEvent

class StartClientUseCase(
    private val channelManager: ChannelManager
) : EventProcessingUseCase<CommandEvent.Connect, Boolean>(CommandEvent.Connect::class) {
    private val commandChannel: BroadcastChannel<CommandEvent> by lazy { channelManager.commandChannel }

    override suspend fun handle(event: CommandEvent.Connect) : UseCaseResult<Boolean> =
        (HeyEddieApplication.clientComponent is SubComponent.NotInitialized)
            .ifTrue {
                HeyEddieApplication.setClient(ClientModule(HeyEddieApplication.applicationComponent.provideApplicationContext()))
            }
            .let {
                when (commandChannel.isClosedForSend) {
                    true -> UseCaseResult.Failure(IllegalStateException("Channel is closed"))
                    false ->
                        commandChannel.send(event)
                            .let { UseCaseResult.Success(true) }
                }
            }
}
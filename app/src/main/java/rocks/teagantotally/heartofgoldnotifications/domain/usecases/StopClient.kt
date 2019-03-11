package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import rocks.teagantotally.heartofgoldnotifications.app.managers.ChannelManager
import rocks.teagantotally.heartofgoldnotifications.domain.framework.EventProcessingUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseResult
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.CommandEvent

class StopClientUseCase(
    private val channelManager: ChannelManager
) : EventProcessingUseCase<CommandEvent.Disconnect, Boolean>(CommandEvent.Disconnect::class) {
    private val commandChannel: BroadcastChannel<CommandEvent> by lazy { channelManager.commandChannel }

    override suspend fun handle(event: CommandEvent.Disconnect): UseCaseResult<Boolean> =
        when (commandChannel.isClosedForSend) {
            true -> UseCaseResult.Failure(IllegalStateException("Channel is closed"))
            false ->
                commandChannel.send(event)
                    .let { UseCaseResult.Success(true) }
        }
}
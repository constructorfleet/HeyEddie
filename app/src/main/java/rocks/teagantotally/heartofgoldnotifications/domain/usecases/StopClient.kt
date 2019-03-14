package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import kotlinx.coroutines.channels.SendChannel
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.ConnectionCommand

class StopClientUseCase(
    private val connectionCommandChannel: SendChannel<ConnectionCommand>
) : UseCase<ConnectionCommand.Disconnect> {
    override suspend fun invoke(parameter: ConnectionCommand.Disconnect) {
        connectionCommandChannel.send(parameter)
    }
}
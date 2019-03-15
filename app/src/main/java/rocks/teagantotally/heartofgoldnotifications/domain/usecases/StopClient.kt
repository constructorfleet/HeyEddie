package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.CommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.ConnectionCommand

class StopClientUseCase(
    private val connectionExecutor: CommandExecutor
) : UseCase<ConnectionCommand.Disconnect> {
    override suspend fun invoke(parameter: ConnectionCommand.Disconnect) {
        connectionExecutor.execute(parameter)
    }
}
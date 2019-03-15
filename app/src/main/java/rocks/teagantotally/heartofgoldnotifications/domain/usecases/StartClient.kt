package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import rocks.teagantotally.heartofgoldnotifications.domain.framework.CommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.ConnectionCommand

class StartClientUseCase(
    private val commandExecutor: CommandExecutor
) : UseCase<ConnectionCommand.Connect> {

    override suspend fun invoke(parameter: ConnectionCommand.Connect) {
        commandExecutor.execute(parameter)
    }
}
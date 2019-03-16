package rocks.teagantotally.heartofgoldnotifications.domain.framework

import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.Command

interface CommandExecutor<CommandType: Command> {
    fun execute(command: CommandType)
}
package rocks.teagantotally.heartofgoldnotifications.domain.framework

import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.Command

interface CommandExecutor {
    fun execute(command: Command)
}
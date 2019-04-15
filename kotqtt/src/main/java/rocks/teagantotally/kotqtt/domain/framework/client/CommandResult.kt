package rocks.teagantotally.kotqtt.domain.framework.client

import rocks.teagantotally.kotqtt.domain.models.commands.MqttCommand
import rocks.teagantotally.kotqtt.domain.models.events.MqttEvent

sealed class CommandResult<CommandType : MqttCommand>(val command: CommandType) : MqttEvent {
    class Success<ResultType, CommandType : MqttCommand>(
        command: CommandType,
        val result: ResultType
    ) : CommandResult<CommandType>(command) {
        override fun toString(): String =
                command.javaClass.simpleName + result.toString()
    }

    class Failure<CommandType : MqttCommand>(
        command: CommandType,
        val throwable: Throwable
    ) : CommandResult<CommandType>(command) {
        override fun toString(): String =
            command.javaClass.simpleName + throwable.message
    }
}
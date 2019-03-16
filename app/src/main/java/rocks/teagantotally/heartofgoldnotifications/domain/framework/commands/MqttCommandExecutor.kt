package rocks.teagantotally.heartofgoldnotifications.domain.framework.commands

import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand

interface MqttCommandExecutor :
    CommandExecutor<MqttCommand>
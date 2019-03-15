package rocks.teagantotally.heartofgoldnotifications.data.managers

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.domain.framework.CommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.ClientCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.Command
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.ConnectionCommand

@ObsoleteCoroutinesApi
class IntentCommandExecutor(
    private val context: Context
) : CommandExecutor {
    override fun execute(command: Command) {
        buildIntent(command)
            ?.let { context.sendBroadcast(it) }
    }

    @UseExperimental(ExperimentalCoroutinesApi::class)
    private fun buildIntent(command: Command) =
        when (command) {
            is ConnectionCommand.Connect ->
                Intent(MqttService.ACTION_CONNECT)
            is ConnectionCommand.Disconnect ->
                Intent(MqttService.ACTION_DISCONNECT)
            is ClientCommand.Publish ->
                Intent(MqttService.ACTION_PUBLISH)
                    .putExtra(
                        MqttService.EXTRA_MESSAGE,
                        command.message as Parcelable
                    )
            else -> null
        }
}
package rocks.teagantotally.heartofgoldnotifications.data.managers

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.domain.framework.MqttCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand

@ObsoleteCoroutinesApi
class IntentMqttCommandExecutor(
    private val context: Context
) : MqttCommandExecutor {
    override fun execute(command: MqttCommand) {
        buildIntent(command)
            ?.let { context.sendBroadcast(it) }
    }

    @UseExperimental(ExperimentalCoroutinesApi::class)
    private fun buildIntent(command: MqttCommand) =
        when (command) {
            is MqttCommand.Connect ->
                Intent(MqttService.ACTION_CONNECT)
            is MqttCommand.Disconnect ->
                Intent(MqttService.ACTION_DISCONNECT)
            is MqttCommand.Publish ->
                Intent(MqttService.ACTION_PUBLISH)
                    .putExtra(
                        MqttService.EXTRA_MESSAGE,
                        command.message as Parcelable
                    )
            else -> null
        }
}
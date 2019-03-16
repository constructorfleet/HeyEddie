package rocks.teagantotally.heartofgoldnotifications.data.managers

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_CONNECT
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_DISCONNECT
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_PUBLISH
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_SUBSCRIBE
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_UNSUBSCRIBE
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_MESSAGE
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_QOS
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_TOPIC
import rocks.teagantotally.heartofgoldnotifications.domain.framework.commands.MqttCommandExecutor
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand

@ObsoleteCoroutinesApi
class IntentMqttCommandExecutor(
    private val context: Context
) : MqttCommandExecutor {
    override fun execute(command: MqttCommand) {
        context.sendBroadcast(buildIntent(command))
    }

    @UseExperimental(ExperimentalCoroutinesApi::class)
    private fun buildIntent(command: MqttCommand) =
        when (command) {
            is MqttCommand.Connect ->
                Intent(ACTION_CONNECT)
            is MqttCommand.Disconnect ->
                Intent(ACTION_DISCONNECT)
            is MqttCommand.Subscribe ->
                Intent(ACTION_SUBSCRIBE)
                    .putExtra(
                        EXTRA_TOPIC,
                        command.topic
                    )
                    .putExtra(
                        EXTRA_QOS,
                        command.maxQoS
                    )
            is MqttCommand.Unsubscribe ->
                Intent(ACTION_UNSUBSCRIBE)
                    .putExtra(
                        EXTRA_TOPIC,
                        command.topic
                    )
            is MqttCommand.Publish ->
                Intent(ACTION_PUBLISH)
                    .putExtra(
                        EXTRA_MESSAGE,
                        command.message as Parcelable
                    )
        }
}
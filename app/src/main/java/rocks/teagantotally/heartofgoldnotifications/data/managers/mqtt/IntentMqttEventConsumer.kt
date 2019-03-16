package rocks.teagantotally.heartofgoldnotifications.data.managers.mqtt

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_COMMAND_FAILED
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_CONNECTED
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_DISCONNECTED
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_MESSAGE_PUBLISHED
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_MESSAGE_RECEIVED
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_SUBSCRIBED
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_UNSUBSCRIBED
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_COMMAND
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_FAILURE_REASON
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_MESSAGE
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_TOPIC
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.MqttEventConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MqttEvent

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class IntentMqttEventConsumer(
    private val context: Context
) : MqttEventConsumer {
    override fun consume(event: MqttEvent) {
        context.sendBroadcast(buildIntent(event))
    }

    private fun buildIntent(event: MqttEvent): Intent =
        when (event) {
            MqttEvent.Connected ->
                Intent(EVENT_CONNECTED)
            MqttEvent.Disconnected ->
                Intent(EVENT_DISCONNECTED)
            is MqttEvent.Subscribed ->
                Intent(EVENT_SUBSCRIBED)
                    .putExtra(
                        EXTRA_TOPIC,
                        event.topic
                    )
            is MqttEvent.Unsubscribed ->
                Intent(EVENT_UNSUBSCRIBED)
                    .putExtra(
                        EXTRA_TOPIC,
                        event.topic
                    )
            is MqttEvent.MessagePublished ->
                Intent(EVENT_MESSAGE_PUBLISHED)
                    .putExtra(
                        EXTRA_MESSAGE,
                        event.message as Parcelable
                    )
            is MqttEvent.MessageReceived ->
                Intent(EVENT_MESSAGE_RECEIVED)
                    .putExtra(
                        EXTRA_MESSAGE,
                        event.message as Parcelable
                    )
            is MqttEvent.CommandFailed ->
                Intent(EVENT_COMMAND_FAILED)
                    .putExtra(
                        EXTRA_COMMAND,
                        event.command as Parcelable
                    )
                    .putExtra(
                        EXTRA_FAILURE_REASON,
                        event.throwable.localizedMessage
                    )
        }
}
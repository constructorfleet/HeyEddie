package rocks.teagantotally.heartofgoldnotifications.data.services.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrue
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_CONNECT
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_DISCONNECT
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_PUBLISH
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_SUBSCRIBE
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.ACTION_UNSUBSCRIBE
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_MESSAGE
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_NOTIFICATION_ID
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_QOS
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_TOPIC
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message

// TODO : Pass generic event processor
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class MqttCommandBroadcastReceiver(
    private val service: MqttService
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            intent?.apply {
                when (action) {
                    ACTION_CONNECT -> service.connect()
                    ACTION_DISCONNECT -> service.disconnect()
                    ACTION_PUBLISH -> {
                        getIntExtra(EXTRA_NOTIFICATION_ID, 0)
                            .ifTrue({ it > 0 }) { service.dismissNotification(it) }

                        getParcelableExtra<Message>(EXTRA_MESSAGE)
                            ?.let { service.publish(it) }
                    }
                    ACTION_SUBSCRIBE ->
                        service.subscribe(
                            getStringExtra(EXTRA_TOPIC),
                            getIntExtra(EXTRA_QOS, 0)
                        )
                    ACTION_UNSUBSCRIBE ->
                        service.unsubscribe(
                            getStringExtra(EXTRA_TOPIC)
                        )
                    else -> null
                } ?: throw IllegalArgumentException()
            }
        } catch (t: Throwable) {
            Timber.e(t) { "Unable to consume action ${intent?.action}" }
        }
    }
}
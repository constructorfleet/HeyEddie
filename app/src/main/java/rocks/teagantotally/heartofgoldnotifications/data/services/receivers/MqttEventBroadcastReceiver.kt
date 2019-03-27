//package rocks.teagantotally.heartofgoldnotifications.data.services.receivers
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import com.github.ajalt.timberkt.Timber
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.ObsoleteCoroutinesApi
//import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_COMMAND_FAILED
//import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_CONNECTED
//import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_DISCONNECTED
//import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_MESSAGE_PUBLISHED
//import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_MESSAGE_RECEIVED
//import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_SUBSCRIBED
//import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_UNSUBSCRIBED
//import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_COMMAND
//import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_FAILURE_REASON
//import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_MESSAGE
//import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EXTRA_TOPIC
//import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.MqttEventConsumer
//import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MqttEvent
//
//@ObsoleteCoroutinesApi
//class MqttEventBroadcastReceiver<MqttEventProcessorType : MqttEventConsumer>(
//    private val mqttEventProcessor: MqttEventProcessorType
//) : BroadcastReceiver() {
//    @UseExperimental(ExperimentalCoroutinesApi::class)
//    override fun onReceive(context: Context?, intent: Intent?) {
//        try {
//            intent?.apply {
//                when (action) {
//                    EVENT_CONNECTED -> MqttEvent.Connected
//                    EVENT_DISCONNECTED -> MqttEvent.Disconnected
//                    EVENT_SUBSCRIBED -> MqttEvent.Subscribed(getStringExtra(EXTRA_TOPIC))
//                    EVENT_UNSUBSCRIBED -> MqttEvent.Unsubscribed(getStringExtra(EXTRA_TOPIC))
//                    EVENT_MESSAGE_PUBLISHED ->
//                        MqttEvent.MessagePublished(getParcelableExtra(EXTRA_MESSAGE))
//                    EVENT_MESSAGE_RECEIVED ->
//                        MqttEvent.MessageReceived(getParcelableExtra(EXTRA_MESSAGE))
//                    EVENT_COMMAND_FAILED ->
//                        MqttEvent.CommandFailed(
//                            getParcelableExtra(EXTRA_COMMAND),
//                            Throwable(getStringExtra(EXTRA_FAILURE_REASON))
//                        )
//                    else -> null
//                }
//                    ?.let { mqttEventProcessor.consume(it) }
//                    ?: throw IllegalArgumentException()
//            }
//        } catch (throwable: Throwable) {
//            Timber.e(throwable)
//        }
//    }
//}
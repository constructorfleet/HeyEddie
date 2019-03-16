//package rocks.teagantotally.heartofgoldnotifications.app.managers
//
//import kotlinx.coroutines.channels.ConflatedBroadcastChannel
//import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand
//import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.NotificationCommand
//import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ConnectionEvent
//import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Failure
//import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MessageEvent
//import rocks.teagantotally.heartofgoldnotifications.domain.models.events.SubscriptionEvent
//
//class ChannelManager {
//    val connectionEventChannel = ConflatedBroadcastChannel<ConnectionEvent>()
//    val messageEventChannel = ConflatedBroadcastChannel<MessageEvent>()
//    val subscriptionEventChannel = ConflatedBroadcastChannel<SubscriptionEvent>()
////    val connectionCommandChannel = ConflatedBroadcastChannel<ConnectionCommand>()
////    val clientCommandChannel = ConflatedBroadcastChannel<MqttCommand>()
////    val notificationCommandChannel = ConflatedBroadcastChannel<NotificationCommand>()
////    val failureEventChannel = ConflatedBroadcastChannel<Failure<*>>()
//}
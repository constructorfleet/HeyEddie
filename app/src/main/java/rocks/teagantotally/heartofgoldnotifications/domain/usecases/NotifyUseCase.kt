package rocks.teagantotally.heartofgoldnotifications.domain.usecases

//class NotifyUseCase(
//    private val gson: Gson,
//    private val notifier: Notifier
//) : ProcessingUseCase<MessageEvent.Received, MessageEvent>(MessageEvent.Received::class) {
//    override suspend fun handle(parameter: MessageEvent.Received) {
//        try {
//            gson.fromJson(parameter.message.payload, NotificationMessage::class.java)
//                .let { notifier.notify(it) }
//        } catch (throwable: Throwable) {
//            Timber.e(throwable)
//        }
//    }
//}
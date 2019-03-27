package rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.receive

//class ProcessMessageReceived(
//    private vararg val messageProcessors: UseCaseWithParameter<Message>
//) : UseCaseWithParameter<MqttMessageReceived> {
//
//    override suspend fun invoke(parameter: MqttMessageReceived) {
//        messageProcessors
//            .forEach { it(parameter.message) }
//    }
//}
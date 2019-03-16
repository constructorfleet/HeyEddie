package rocks.teagantotally.heartofgoldnotifications.domain.usecases

//class ProcessMessage(
//    private vararg val messageProcessors: ProcessingUseCase<*, MessageEvent.Received>
//) : UseCase<MessageEvent.Received> {
//
//    override suspend fun invoke(parameter: MessageEvent.Received) {
//        (parameter !is MessageEvent.Received.Failed)
//            .ifTrue {
//                messageProcessors
//                    .forEach { it(parameter) }
//            }
//    }
//}
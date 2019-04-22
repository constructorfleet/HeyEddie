package rocks.teagantotally.heartofgoldnotifications.domain.models.messages

interface ReceivedMessage {
    var id: Int?
    val title: String
    val body: String
    val topic: String
}
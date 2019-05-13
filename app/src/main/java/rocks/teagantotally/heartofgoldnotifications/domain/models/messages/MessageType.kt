package rocks.teagantotally.heartofgoldnotifications.domain.models.messages

enum class MessageType(
    val messageClass: Class<*>
) {
    ALL(ReceivedMessage::class.java),
    NOTIFICATION(NotificationMessage::class.java)
}
package rocks.teagantotally.heartofgoldnotifications.domain.models.messages

enum class MessageType(val messageClass: Class<*>) {
    ALL(String::class.java),
    NOTIFICATION(NotificationMessage::class.java)
}
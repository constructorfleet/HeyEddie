package rocks.teagantotally.heartofgoldnotifications.domain.models.messages

import rocks.teagantotally.kotqtt.domain.models.Message

enum class MessageType(val messageClass: Class<*>) {
    ALL(Message::class.java),
    NOTIFICATION(NotificationMessage::class.java)
}
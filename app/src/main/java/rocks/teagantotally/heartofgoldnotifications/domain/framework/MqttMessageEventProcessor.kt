package rocks.teagantotally.heartofgoldnotifications.domain.framework

import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MessageEvent

interface MqttMessageProcessor<MessageEventType : MessageEvent> : EventProcessor<MessageEventType>
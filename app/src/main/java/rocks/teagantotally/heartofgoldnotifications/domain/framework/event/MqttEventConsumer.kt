package rocks.teagantotally.heartofgoldnotifications.domain.framework.event

import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MqttEvent

interface MqttEventConsumer : EventConsumer<MqttEvent>
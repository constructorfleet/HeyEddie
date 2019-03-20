package rocks.teagantotally.heartofgoldnotifications.domain.framework.event.device

import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.EventConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.DeviceEvent

interface DeviceEventConsumer<DeviceEventType : DeviceEvent> :
    EventConsumer<DeviceEventType>
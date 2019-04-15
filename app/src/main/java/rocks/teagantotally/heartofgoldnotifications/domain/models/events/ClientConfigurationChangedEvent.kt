package rocks.teagantotally.heartofgoldnotifications.domain.models.events

import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Event

class ClientConfigurationChangedEvent(
    old: ConnectionConfiguration? = null,
    new: ConnectionConfiguration
) : ConfigurationChangedEvent<ConnectionConfiguration>(old, new)
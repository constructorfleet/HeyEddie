package rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.pre

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.Configuration
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ConfigurationChangedEvent

interface PreConfigurationChangedUseCase<EventType : ConfigurationChangedEvent<ConfigurationType>, ConfigurationType : Configuration> :
    UseCaseWithParameter<EventType>
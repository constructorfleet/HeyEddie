package rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.post

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.Configuration
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ConfigurationChangedEvent

interface PostConfigurationChangedUseCase<EventType : ConfigurationChangedEvent<ConfigurationType>, ConfigurationType : Configuration> :
    UseCaseWithParameter<EventType>
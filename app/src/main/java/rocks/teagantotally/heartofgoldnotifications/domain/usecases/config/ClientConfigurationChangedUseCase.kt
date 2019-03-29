package rocks.teagantotally.heartofgoldnotifications.domain.usecases.config

import kotlinx.coroutines.channels.BroadcastChannel
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseChannel
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.ClientConfigurationChangedEvent

class ClientConfigurationChangedUseCase(
    broadcastChannel: BroadcastChannel<ClientConfigurationChangedEvent>
) : UseCaseChannel<ClientConfigurationChangedEvent>,
    BroadcastChannel<ClientConfigurationChangedEvent> by broadcastChannel
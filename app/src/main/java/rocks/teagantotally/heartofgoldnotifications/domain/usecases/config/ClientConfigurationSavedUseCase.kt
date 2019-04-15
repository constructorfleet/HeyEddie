package rocks.teagantotally.heartofgoldnotifications.domain.usecases.config

import kotlinx.coroutines.channels.BroadcastChannel
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseChannel
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientConfigurationChangedEvent

class ClientConfigurationSavedUseCase(
    broadcastChannel: BroadcastChannel<ClientConfigurationChangedEvent>
) : UseCaseChannel<ClientConfigurationChangedEvent>,
    BroadcastChannel<ClientConfigurationChangedEvent> by broadcastChannel
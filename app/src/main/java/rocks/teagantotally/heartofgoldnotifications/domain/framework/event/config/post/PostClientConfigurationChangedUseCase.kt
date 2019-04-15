package rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.post

import kotlinx.coroutines.CoroutineScope
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientContainer
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.ClientConfigurationChangedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.ClientConfigurationChangedEvent
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.config.ClientConfigurationSavedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.connection.ConnectClient
import javax.inject.Inject

class PostClientConfigurationChangedUseCase(
    private val clientConfigurationSavedUseCase: ClientConfigurationSavedUseCase,
    coroutineScope: CoroutineScope
) : ClientConfigurationChangedUseCase,
    PostConnectionConfigurationChangedUseCase,
    CoroutineScope by coroutineScope {
    @Inject
    lateinit var clientContainer: ClientContainer

    private val connectClient: ConnectClient
        get() = clientContainer.connectClient

    override suspend fun invoke(parameter: ClientConfigurationChangedEvent) {
        HeyEddieApplication
            .clientComponent
            ?.inject(this)
            ?.run {
                if (!clientConfigurationSavedUseCase.isClosedForSend) {
                    clientConfigurationSavedUseCase.send(parameter)
                }
            }
    }
}
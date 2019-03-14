package rocks.teagantotally.heartofgoldnotifications.domain.usecases

import kotlinx.coroutines.channels.SendChannel
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.SubComponent
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrue
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientModule
import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.ConnectionCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.Failure

class StartClientUseCase(
    private val connectionCommandChannel: SendChannel<ConnectionCommand.Connect>,
    private val failureEventChannel: SendChannel<Failure<*>>
) : UseCase<ConnectionCommand.Connect> {

    override suspend fun invoke(parameter: ConnectionCommand.Connect) {
        (HeyEddieApplication.clientComponent is SubComponent.NotInitialized)
            .ifTrue {
                HeyEddieApplication.setClient(ClientModule(HeyEddieApplication.applicationComponent.provideApplicationContext()))
            }
            .let {
                when (connectionCommandChannel.isClosedForSend) {
                    true -> return
                    false -> connectionCommandChannel.send(parameter)
                }
            }
    }
}
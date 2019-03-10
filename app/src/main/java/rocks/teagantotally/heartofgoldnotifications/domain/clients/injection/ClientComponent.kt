package rocks.teagantotally.heartofgoldnotifications.domain.clients.injection

import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.SessionScope
import rocks.teagantotally.heartofgoldnotifications.data.services.EventService
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService

@SessionScope
@Subcomponent(
    modules = [ClientModule::class]
)
interface ClientComponent {
    fun inject(service: MqttService)
    fun inject(service: EventService)

    @Subcomponent.Builder
    interface Builder {
        fun clientModule(module: ClientModule): Builder

        fun build(): ClientComponent
    }
}
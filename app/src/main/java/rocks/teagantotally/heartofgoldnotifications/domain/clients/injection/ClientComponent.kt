package rocks.teagantotally.heartofgoldnotifications.domain.clients.injection

import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.SessionScope
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.presentation.status.StatusFragment

@SessionScope
@Subcomponent(
    modules = [ClientModule::class]
)
interface ClientComponent {
    fun inject(service: MqttService)

    @Subcomponent.Builder
    interface Builder {
        fun clientModule(module: ClientModule): Builder

        fun build(): ClientComponent
    }
}
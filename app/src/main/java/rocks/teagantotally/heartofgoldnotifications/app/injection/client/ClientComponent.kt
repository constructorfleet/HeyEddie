package rocks.teagantotally.heartofgoldnotifications.app.injection.client

import dagger.Component
import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.SessionScope
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration

@SessionScope
@Component(
    modules = [ClientModule::class],
    dependencies = [ApplicationComponent::class]
)
interface ClientComponent {
    fun inject(receiver: MqttService.PublishReceiver)

    fun provideClientContainer(): ClientContainer
    fun provideConnectionConfiguration(): ConnectionConfiguration
}
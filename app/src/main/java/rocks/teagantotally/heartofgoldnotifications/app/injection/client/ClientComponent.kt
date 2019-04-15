package rocks.teagantotally.heartofgoldnotifications.app.injection.client

import dagger.Component
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.SessionScope
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.post.PostClientConfigurationChangedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.config.pre.PreConnectionChangedUseCase
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.connection.GetClientStatus

@SessionScope
@Component(
    modules = [ClientModule::class],
    dependencies = [ApplicationComponent::class]
)
interface ClientComponent {
    fun inject(receiver: MqttService.PublishReceiver)
    fun inject(usecase: PreConnectionChangedUseCase)
    fun inject(usecase: PostClientConfigurationChangedUseCase)
    fun inject(usecase: GetClientStatus)

    fun provideClientContainer(): ClientContainer
    fun provideConnectionConfiguration(): ConnectionConfiguration
}
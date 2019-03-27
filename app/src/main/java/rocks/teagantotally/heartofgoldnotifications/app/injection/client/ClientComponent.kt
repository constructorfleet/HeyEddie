package rocks.teagantotally.heartofgoldnotifications.app.injection.client

import dagger.Component
import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.SessionScope

@SessionScope
@Component(
    modules = [ClientModule::class],
    dependencies = [ApplicationComponent::class]
)
interface ClientComponent {
//    fun inject(clientContainer: ClientContainer)
    fun provideClientContainer(): ClientContainer
}
package rocks.teagantotally.heartofgoldnotifications.app.injection

import dagger.Component
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientComponent
import rocks.teagantotally.heartofgoldnotifications.presentation.MainActivity
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ApplicationModule::class]
)
interface ApplicationComponent {
    fun clientComponentBuilder(): ClientComponent.Builder
}
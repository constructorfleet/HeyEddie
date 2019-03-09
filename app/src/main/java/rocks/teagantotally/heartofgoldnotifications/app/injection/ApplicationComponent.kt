package rocks.teagantotally.heartofgoldnotifications.app.injection

import dagger.Component
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientComponent
import rocks.teagantotally.heartofgoldnotifications.domain.clients.injection.ClientModule
import rocks.teagantotally.heartofgoldnotifications.presentation.injection.MainActivityComponent
import rocks.teagantotally.heartofgoldnotifications.presentation.status.StatusFragment
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ApplicationModule::class]
)
interface ApplicationComponent {
    fun inject(fragment: StatusFragment)

    fun clientComponentBuilder(): ClientComponent.Builder

    fun mainActivityComponentBuilder(): MainActivityComponent.Builder
}
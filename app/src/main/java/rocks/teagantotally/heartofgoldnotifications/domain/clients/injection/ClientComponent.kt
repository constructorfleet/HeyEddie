package rocks.teagantotally.heartofgoldnotifications.domain.clients.injection

import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.SessionScope

@SessionScope
@Subcomponent(
    modules = [ClientModule::class]
)
interface ClientComponent {
//    fun inject(activity: MainActivity)

    @Subcomponent.Builder
    interface Builder {
        fun clientModule(module: ClientModule): Builder

        fun build(): ClientComponent
    }
}
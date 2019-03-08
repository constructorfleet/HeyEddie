package rocks.teagantotally.heartofgoldnotifications.domain.clients.injection

import dagger.Component
import dagger.Subcomponent
import kotlinx.coroutines.channels.Channel
import rocks.teagantotally.heartofgoldnotifications.app.injection.ApplicationComponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.Event
import rocks.teagantotally.heartofgoldnotifications.app.injection.qualifiers.Message
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.SessionScope
import rocks.teagantotally.heartofgoldnotifications.domain.models.ClientEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.MessageEvent
import rocks.teagantotally.heartofgoldnotifications.presentation.MainActivity

@SessionScope
@Subcomponent(
    modules = [ClientModule::class]
)
interface ClientComponent {
    fun inject(activity: MainActivity)

    @Subcomponent.Builder
    interface Builder {
        fun clientModule(module: ClientModule): Builder

        fun build(): ClientComponent
    }
}
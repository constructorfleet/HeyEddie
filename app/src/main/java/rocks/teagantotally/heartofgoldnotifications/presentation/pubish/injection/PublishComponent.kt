package rocks.teagantotally.heartofgoldnotifications.presentation.pubish.injection

import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.presentation.pubish.PublishFragment

@FragmentScope
@Subcomponent(modules = [PublishModule::class])
interface PublishComponent {
    fun inject(fragment: PublishFragment)

    @Subcomponent.Builder
    interface Builder {
        fun module(module: PublishModule): Builder

        fun build(): PublishComponent
    }
}
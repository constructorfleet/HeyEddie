package rocks.teagantotally.heartofgoldnotifications.presentation.status.injection

import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.presentation.status.StatusFragment

@FragmentScope
@Subcomponent(modules = [StatusModule::class])
interface StatusComponent {
    fun inject(fragment: StatusFragment)

    @Subcomponent.Builder
    interface Builder {
        fun module(module: StatusModule): Builder

        fun build(): StatusComponent
    }
}
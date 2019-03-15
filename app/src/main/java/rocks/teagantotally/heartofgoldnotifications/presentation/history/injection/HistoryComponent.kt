package rocks.teagantotally.heartofgoldnotifications.presentation.history.injection

import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.presentation.history.HistoryFragment

@FragmentScope
@Subcomponent(modules = [HistoryModule::class])
interface StatusComponent {
    fun inject(fragment: HistoryFragment)

    @Subcomponent.Builder
    interface Builder {
        fun module(module: HistoryModule): Builder

        fun build(): StatusComponent
    }
}
package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.history.injection

import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.history.HistoryFragment

@FragmentScope
@Subcomponent(modules = [HistoryModule::class])
interface HistoryComponent {
    fun inject(fragment: HistoryFragment)

    @Subcomponent.Builder
    interface Builder {
        fun module(module: HistoryModule): Builder

        fun build(): HistoryComponent
    }
}
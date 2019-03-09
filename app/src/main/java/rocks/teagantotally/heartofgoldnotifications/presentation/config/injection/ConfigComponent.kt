package rocks.teagantotally.heartofgoldnotifications.presentation.config.injection

import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.presentation.config.ConfigFragment

@FragmentScope
@Subcomponent(modules = [ConfigModule::class])
interface ConfigComponent {
    fun inject(fragment: ConfigFragment)

    @Subcomponent.Builder
    interface Builder {
        fun module(module: ConfigModule): Builder

        fun build(): ConfigComponent
    }
}
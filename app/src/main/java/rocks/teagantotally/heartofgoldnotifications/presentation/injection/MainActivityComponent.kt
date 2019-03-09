package rocks.teagantotally.heartofgoldnotifications.presentation.injection

import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.ActivityScope
import rocks.teagantotally.heartofgoldnotifications.presentation.MainActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.config.injection.ConfigComponent

@ActivityScope
@Subcomponent(modules = [MainActivityModule::class])
interface MainActivityComponent {
    fun inject(activity: MainActivity)

    fun configComponentBuilder(): ConfigComponent.Builder

    @Subcomponent.Builder
    interface Builder {
        fun module(mainActivityModule: MainActivityModule): Builder

        fun build(): MainActivityComponent
    }
}
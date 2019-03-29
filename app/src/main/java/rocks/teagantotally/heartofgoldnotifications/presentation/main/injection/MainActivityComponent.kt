package rocks.teagantotally.heartofgoldnotifications.presentation.main.injection

import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.ActivityScope
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.injection.ConfigComponent
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.history.injection.HistoryComponent
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.pubish.injection.PublishComponent
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.subscriptions.injection.SubscriptionsComponent

@ActivityScope
@Subcomponent(modules = [MainActivityModule::class])
interface MainActivityComponent {
    fun inject(activity: MainActivity)

    fun configComponentBuilder(): ConfigComponent.Builder

    fun historyComponentBuilder(): HistoryComponent.Builder

    fun subscriptionComponentBuilder(): SubscriptionsComponent.Builder

    fun publishComponentBuilder(): PublishComponent.Builder

    @Subcomponent.Builder
    interface Builder {
        fun module(mainActivityModule: MainActivityModule): Builder

        fun build(): MainActivityComponent
    }
}
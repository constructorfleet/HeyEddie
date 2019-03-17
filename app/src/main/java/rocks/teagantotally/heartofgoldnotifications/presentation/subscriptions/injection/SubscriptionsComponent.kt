package rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.injection

import dagger.Subcomponent
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.SubscriptionsFragment

@Subcomponent(modules = [SubscriptionModule::class])
@FragmentScope
interface SubscriptionsComponent {
    fun inject(fragment: SubscriptionsFragment)

    @Subcomponent.Builder
    interface Builder {
        fun module(module: SubscriptionModule): Builder

        fun build(): SubscriptionsComponent
    }

}
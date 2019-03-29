package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.subscriptions.injection

import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.SubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.subscriptions.SubscriptionsContract
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.subscriptions.SubscriptionsPresenter

@Module
class SubscriptionModule(
    private val view: SubscriptionsContract.View
) {
    @Provides
    @FragmentScope
    fun providePresenter(
        subscriptionManager: SubscriptionManager
    ): SubscriptionsContract.Presenter =
        SubscriptionsPresenter(
            view,
            subscriptionManager,
            view
        )
}
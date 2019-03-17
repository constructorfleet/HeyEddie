package rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.injection

import dagger.Module
import dagger.Provides
import rocks.teagantotally.heartofgoldnotifications.app.injection.scopes.FragmentScope
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.SubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.SubscribeTo
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription.UnsubscribeFrom
import rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.SubscriptionsContract
import rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.SubscriptionsPresenter

@Module
class SubscriptionModule(
    private val view: SubscriptionsContract.View
) {
    @Provides
    @FragmentScope
    fun providePresenter(
        subscriptionManager: SubscriptionManager,
        subscribeTo: SubscribeTo,
        unsubscribeFrom: UnsubscribeFrom
    ): SubscriptionsContract.Presenter =
        SubscriptionsPresenter(
            view,
            subscriptionManager,
            subscribeTo,
            unsubscribeFrom
        )
}
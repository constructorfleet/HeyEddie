package rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCase
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.SubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.SubscriptionConfiguration

class AddSubscription(
    private val subscriptionManager: SubscriptionManager
) : UseCase<SubscriptionConfiguration> {
    override suspend fun invoke(parameter: SubscriptionConfiguration) {
        subscriptionManager.addSubscription(parameter)
    }
}
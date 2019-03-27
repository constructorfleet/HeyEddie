package rocks.teagantotally.heartofgoldnotifications.domain.usecases.subscription

import rocks.teagantotally.heartofgoldnotifications.domain.framework.UseCaseWithParameter
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.SubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.SubscriptionConfiguration
import javax.inject.Inject

class AddSubscription @Inject constructor(
    private val subscriptionManager: SubscriptionManager
) : UseCaseWithParameter<SubscriptionConfiguration> {
    override suspend fun invoke(parameter: SubscriptionConfiguration) {
        subscriptionManager.addSubscription(parameter)
    }
}
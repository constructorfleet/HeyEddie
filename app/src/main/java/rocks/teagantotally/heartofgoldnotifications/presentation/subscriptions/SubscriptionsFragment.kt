package rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions

import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.SubscriptionConfiguration
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import javax.inject.Inject

class SubscriptionsFragment : BaseFragment(), SubscriptionsContract.View, Scoped {
    @Inject
    override lateinit var presenter: SubscriptionsContract.Presenter

    override val navigationMenuId: Int =
        R.id.menu_item_subscriptions

    override fun displaySubscription(subscription: SubscriptionConfiguration) {

    }

    override fun showLoading(loading: Boolean) {

    }

    override fun showError(message: String?) {

    }
}
package rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions

import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BasePresenter
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseView
import rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.viewmodels.SubscriptionViewModel

interface SubscriptionsContract {
    interface View : BaseView<Presenter> {
        fun displaySubscription(subscription: SubscriptionViewModel)

        fun removeSubscription(subscription: SubscriptionViewModel)

        fun showCreateNewSubscription()

        fun newSubscriptionSaved()

        fun promptToDelete(subscription: SubscriptionViewModel.ActiveSubscription)

        fun promptToCancel(subscription: SubscriptionViewModel.NewSubscription)
    }

    interface Presenter : BasePresenter {
        fun onShowCreateNewSubscription()

        fun onDeleteSubscription(subscription: SubscriptionViewModel.ActiveSubscription)

        fun onCancelNewSubscription(subscription: SubscriptionViewModel.NewSubscription)

        fun saveNewSubscription(
            topic: String,
            maxQoS: Int,
            messageType: MessageType
        )

        fun removeSubscription(
            topic: String,
            maxQoS: Int,
            messageType: MessageType
        )
    }
}
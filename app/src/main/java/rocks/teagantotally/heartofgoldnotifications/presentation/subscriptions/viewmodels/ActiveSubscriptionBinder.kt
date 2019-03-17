package rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.viewmodels

import android.view.View
import kotlinx.android.synthetic.main.item_active_subscription.view.*
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.ConditionalItemBinder

class ActiveSubscriptionBinder : ConditionalItemBinder<SubscriptionViewModel> {
    override fun canBind(item: SubscriptionViewModel): Boolean =
        item is SubscriptionViewModel.ActiveSubscription

    override fun getLayoutResourceId(item: SubscriptionViewModel): Int =
        R.layout.item_active_subscription

    override fun bind(item: SubscriptionViewModel, view: View) {
        with(view) {
            (item as? SubscriptionViewModel.ActiveSubscription)
                ?.let {
                    subscription_topic.text = it.topic
                    subscription_max_qos.text = it.maxQoS.toString()
                    subscription_message_type.text = it.messageType.name
                }
        }
    }
}
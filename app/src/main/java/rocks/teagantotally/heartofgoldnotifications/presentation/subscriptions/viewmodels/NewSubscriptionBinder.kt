package rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.viewmodels

import android.text.Editable
import android.view.View
import kotlinx.android.synthetic.main.item_add_subscription.view.*
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType
import rocks.teagantotally.heartofgoldnotifications.presentation.common.OptionsMenuCallbacks
import rocks.teagantotally.heartofgoldnotifications.presentation.common.SimpleTextWatcher
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.ConditionalItemBinder
import rocks.teagantotally.heartofgoldnotifications.presentation.views.SimpleTypedSpinner

class NewSubscriptionBinder(private val optionsMenuCallbacks: OptionsMenuCallbacks) :
    ConditionalItemBinder<SubscriptionViewModel> {
    override fun canBind(item: SubscriptionViewModel): Boolean =
        item is SubscriptionViewModel.NewSubscription

    override fun getLayoutResourceId(item: SubscriptionViewModel): Int =
        R.layout.item_add_subscription

    override fun bind(item: SubscriptionViewModel, view: View) {
        with(view) {
            (item as? SubscriptionViewModel.NewSubscription)
                ?.let { subscription ->
                    new_subscription_topic.addTextChangedListener(TopicTextWatcher(subscription, optionsMenuCallbacks))
                    subscription.topic
                        ?.let {
                            new_subscription_topic.text =
                                Editable.Factory
                                    .getInstance()
                                    .newEditable(subscription.topic)
                        }

                    with(new_subscription_max_qos) {
                        setItemSelectedListener(
                            MaxQosItemSelectedListener(
                                subscription,
                                optionsMenuCallbacks
                            )
                        )
                        setSelection(subscription.maxQoS ?: 0, true)
                    }

                    with(new_subscription_message_type) {
                        setItemSelectedListener(
                            MessageTypeItemSelectedListener(
                                subscription,
                                optionsMenuCallbacks
                            )
                        )
                        selectItem(subscription.messageType)
                    }
                }
        }
    }

    private class TopicTextWatcher(
        private val item: SubscriptionViewModel.NewSubscription,
        private val optionsMenuCallbacks: OptionsMenuCallbacks
    ) : SimpleTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            item.topic = s?.toString()
            optionsMenuCallbacks.invalidateOptionsMenu()
        }
    }

    private class MaxQosItemSelectedListener(
        private val subscription: SubscriptionViewModel.NewSubscription,
        private val optionsMenuCallbacks: OptionsMenuCallbacks
    ) : SimpleTypedSpinner.OnItemSelectedListener<Int> {
        override fun onItemSelected(item: Int?) {
            subscription.maxQoS = item
            optionsMenuCallbacks.invalidateOptionsMenu()
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private class MessageTypeItemSelectedListener(
        private val subscription: SubscriptionViewModel.NewSubscription,
        private val optionsMenuCallbacks: OptionsMenuCallbacks
    ) : SimpleTypedSpinner.OnItemSelectedListener<MessageType> {
        override fun onItemSelected(item: MessageType?) {
            subscription.messageType = item
        }
    }
}
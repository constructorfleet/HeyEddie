package rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.viewmodels

import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_add_subscription.view.*
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrueMaybe
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType
import rocks.teagantotally.heartofgoldnotifications.presentation.common.OptionsMenuCallbacks
import rocks.teagantotally.heartofgoldnotifications.presentation.common.SimpleTextWatcher
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.ConditionalItemBinder

class NewSubscriptionBinder(private val optionsMenuCallbacks: OptionsMenuCallbacks) :
    ConditionalItemBinder<SubscriptionViewModel> {
    override fun canBind(item: SubscriptionViewModel): Boolean =
        item is SubscriptionViewModel.NewSubscription

    override fun getLayoutResourceId(item: SubscriptionViewModel): Int =
        R.layout.item_add_subscription

    override fun bind(item: SubscriptionViewModel, view: View) {
        with(view) {
            (item as? SubscriptionViewModel.NewSubscription)
                ?.let {
                    new_subscription_topic.addTextChangedListener(TopicTextWatcher(it, optionsMenuCallbacks))
                    it.topic
                        ?.let {
                            new_subscription_topic.text =
                                Editable.Factory
                                    .getInstance()
                                    .newEditable(it)
                        }

                    val qosOptions =
                        context.resources
                            .getIntArray(R.array.mqtt_qos)
                            .map { it.toString() }
                            .toTypedArray()

                    with(new_subscription_max_qos) {
                        adapter =
                            ArrayAdapter(
                                context,
                                android.R.layout.simple_spinner_item,
                                qosOptions
                            ).also { arrayAdapter ->
                                onItemSelectedListener =
                                    MaxQosItemSelectedListener(
                                        arrayAdapter,
                                        it,
                                        optionsMenuCallbacks
                                    )
                            }
                        setSelection(qosOptions.indexOf(it.maxQoS?.toString()))
                    }

                    val messageTypeOptions =
                        MessageType
                            .values()
                            .map { it.name }
                            .toTypedArray()

                    with(new_subscription_message_type) {
                        adapter =
                            ArrayAdapter(
                                context,
                                android.R.layout.simple_spinner_item,
                                messageTypeOptions
                            ).also { arrayAdapter ->
                                onItemSelectedListener =
                                    MessageTypeItemSelectedListener(
                                        arrayAdapter,
                                        it,
                                        optionsMenuCallbacks
                                    )
                            }
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

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private class MaxQosItemSelectedListener(
        private val adapter: ArrayAdapter<String>,
        private val item: SubscriptionViewModel.NewSubscription,
        private val optionsMenuCallbacks: OptionsMenuCallbacks
    ) : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            item.maxQoS = null
            optionsMenuCallbacks.invalidateOptionsMenu()
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            item.maxQoS =
                (position in 0..(adapter.count - 1))
                    .ifTrueMaybe { adapter.getItem(position)?.toIntOrNull() }
            optionsMenuCallbacks.invalidateOptionsMenu()
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private class MessageTypeItemSelectedListener(
        private val adapter: ArrayAdapter<String>,
        private val item: SubscriptionViewModel.NewSubscription,
        private val optionsMenuCallbacks: OptionsMenuCallbacks
    ) : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            item.messageType = null
            optionsMenuCallbacks.invalidateOptionsMenu()
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            item.messageType = (position in 0..(adapter.count - 1))
                .ifTrueMaybe { adapter.getItem(position) }
                ?.let { MessageType.valueOf(it) }

            optionsMenuCallbacks.invalidateOptionsMenu()
        }
    }
}
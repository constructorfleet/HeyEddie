package rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_subscriptions.*
import kotlinx.android.synthetic.main.item_active_subscription.view.*
import kotlinx.android.synthetic.main.item_add_subscription.view.*
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.common.extensions.safeLet
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.CompositeItemBinder
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.ConditionalItemBinder
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.SelfBindingRecyclerAdapter
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.injection.SubscriptionModule
import javax.inject.Inject

class SubscriptionsFragment : BaseFragment(), SubscriptionsContract.View, Scoped {
    @Inject
    override lateinit var presenter: SubscriptionsContract.Presenter
    private val isAdding: Boolean
        get() = subscriptionsAdapter.items.firstOrNull() is SubscriptionViewModel.NewSubscription
    private val isValid: Boolean
        get() =
            isAdding && (subscriptionsAdapter.items.firstOrNull() as? SubscriptionViewModel.NewSubscription)?.isValid == true

    private val subscriptionsBinder: CompositeItemBinder<SubscriptionViewModel> =
        CompositeItemBinder(
            ActiveSubscriptionBinder,
            NewSubscriptionBinder(this)
        )
    private val subscriptionsAdapter: SelfBindingRecyclerAdapter<SubscriptionViewModel> =
        SelfBindingRecyclerAdapter(subscriptionsBinder)

    override val navigationMenuId: Int =
        R.id.menu_item_subscriptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_subscriptions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MainActivity.mainActivityComponent
            .subscriptionComponentBuilder()
            .module(SubscriptionModule(this))
            .build()
            .inject(this)

        with(subscriptions) {
            adapter = subscriptionsAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        presenter.onViewCreated()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_subsciptions, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.findItem(R.id.menu_item_new_subscription)
            ?.isVisible = !isAdding
        menu?.findItem(R.id.menu_item_add_subscription)
            ?.let {
                it.isVisible = isAdding
                it.isEnabled = isValid
                it.icon.alpha =
                    when (isValid) {
                        true -> 255
                        false -> 127
                    }
            }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        when (item?.itemId) {
            R.id.menu_item_new_subscription ->
                presenter.onCreateSubscription()
            R.id.menu_item_add_subscription ->
                (subscriptionsAdapter.items[0] as? SubscriptionViewModel.NewSubscription)
                    ?.let {
                        safeLet(it.topic, it.maxQoS) { topic, qos ->
                            presenter.addSubscription(
                                topic,
                                qos,
                                MessageType.NOTIFICATION
                            )
                        }
                    }
            else -> null
        }
            ?.run { true }
            ?: false

    override fun displaySubscription(subscription: SubscriptionViewModel) {
        subscriptionsAdapter.add(subscription)
    }

    override fun removeSubscription(subscription: SubscriptionViewModel) {
        subscriptionsAdapter.remove(subscription)
    }

    override fun showNewSubscription() {
        SubscriptionViewModel.NewSubscription(
            maxQoS = 0,
            messageType = MessageType.NOTIFICATION
        )
            .let {
                subscriptionsAdapter.add(it, 0)
                invalidateOptionsMenu()
            }
    }

    override fun newSubscriptionSaved(subscription: SubscriptionViewModel) {
        subscriptionsAdapter.remove(0)
        subscriptionsAdapter.add(subscription, 0)
        invalidateOptionsMenu()
    }

    override fun showLoading(loading: Boolean) {

    }

    override fun showError(message: String?) {

    }

    internal fun invalidateOptionsMenu() {
        activity?.invalidateOptionsMenu()
    }

    class NewSubscriptionBinder(val fragment: SubscriptionsFragment) : ConditionalItemBinder<SubscriptionViewModel> {
        override fun canBind(item: SubscriptionViewModel): Boolean =
            item is SubscriptionViewModel.NewSubscription

        override fun getLayoutResourceId(item: SubscriptionViewModel): Int =
            R.layout.item_add_subscription

        override fun bind(item: SubscriptionViewModel, view: View) {
            with(view) {
                (item as? SubscriptionViewModel.NewSubscription)
                    ?.let {
                        it.topic
                            ?.let {
                                new_subscription_topic.text =
                                    Editable.Factory
                                        .getInstance()
                                        .newEditable(it)
                            }
                        new_subscription_topic.addTextChangedListener(
                            object : TextWatcher {
                                override fun afterTextChanged(s: Editable?) {
                                    item.topic = s?.toString()
                                    fragment.invalidateOptionsMenu()
                                }

                                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                                }

                                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                                }
                            }
                        )
                        new_subscription_max_qos.progress = it.maxQoS ?: 0
                        new_subscription_max_qos.setOnSeekBarChangeListener(
                            object : SeekBar.OnSeekBarChangeListener {
                                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                    item.maxQoS = progress
                                    fragment.invalidateOptionsMenu()
                                }

                                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                                }

                                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                                }
                            }
                        )

                        fragment.invalidateOptionsMenu()
                    }
            }
        }
    }

    object ActiveSubscriptionBinder : ConditionalItemBinder<SubscriptionViewModel> {
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
                    }
            }
        }
    }
}
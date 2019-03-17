package rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import kotlinx.android.synthetic.main.fragment_subscriptions.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.common.extensions.safeLet
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.common.OptionsMenuCallbacks
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.AnimatedLinearLayoutManager
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.CompositeItemBinder
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.SelfBindingRecyclerAdapter
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.injection.SubscriptionModule
import rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.viewmodels.ActiveSubscriptionBinder
import rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.viewmodels.NewSubscriptionBinder
import rocks.teagantotally.heartofgoldnotifications.presentation.subscriptions.viewmodels.SubscriptionViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class SubscriptionsFragment : BaseFragment(), SubscriptionsContract.View,
    OptionsMenuCallbacks {
    @Inject
    override lateinit var presenter: SubscriptionsContract.Presenter
    private val isAdding: Boolean
        get() = subscriptionsAdapter.items.firstOrNull() is SubscriptionViewModel.NewSubscription
    private val isValid: Boolean
        get() =
            isAdding && (subscriptionsAdapter.items.firstOrNull() as? SubscriptionViewModel.NewSubscription)?.isValid == true

    private val subscriptionsBinder: CompositeItemBinder<SubscriptionViewModel> =
        CompositeItemBinder(
            ActiveSubscriptionBinder(),
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
            layoutManager = AnimatedLinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            setHasFixedSize(false)
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
                presenter.onShowCreateNewSubscription()
            R.id.menu_item_add_subscription ->
                (subscriptionsAdapter.items[0] as? SubscriptionViewModel.NewSubscription)
                    ?.let {
                        safeLet(it.topic, it.maxQoS) { topic, qos ->
                            presenter.saveNewSubscription(
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
        launch {
            subscriptionsAdapter.add(subscription)
        }
    }

    override fun removeSubscription(subscription: SubscriptionViewModel) {
        launch {
            subscriptionsAdapter.remove(subscription)
        }
    }

    override fun showCreateNewSubscription() {
        SubscriptionViewModel.NewSubscription(
            maxQoS = 0,
            messageType = MessageType.NOTIFICATION
        )
            .let {
                launch {
                    subscriptionsAdapter.add(it, 0)
                }
            }
    }

    override fun newSubscriptionSaved(subscription: SubscriptionViewModel) {
        launch {
            subscriptionsAdapter.remove(0)
            subscriptionsAdapter.add(subscription, 0)
        }
        invalidateOptionsMenu()
    }

    override fun showLoading(loading: Boolean) {

    }

    override fun showError(message: String?) {

    }

    override fun invalidateOptionsMenu() {
        activity?.invalidateOptionsMenu()
    }
}
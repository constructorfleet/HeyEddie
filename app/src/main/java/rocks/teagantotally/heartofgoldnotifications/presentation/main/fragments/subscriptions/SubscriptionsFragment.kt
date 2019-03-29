package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.subscriptions

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import kotlinx.android.synthetic.main.fragment_subscriptions.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.common.extensions.safeLet
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.common.OptionsMenuCallbacks
import rocks.teagantotally.heartofgoldnotifications.presentation.common.annotations.ActionBarTitle
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.AnimatedLinearLayoutManager
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.CompositeItemBinder
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.SelfBindingRecyclerAdapter
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.subscriptions.injection.SubscriptionModule
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.subscriptions.viewmodels.ActiveSubscriptionBinder
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.subscriptions.viewmodels.NewSubscriptionBinder
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.subscriptions.viewmodels.SubscriptionViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@ActionBarTitle(R.string.title_subscriptions)
class SubscriptionsFragment : BaseFragment(), SubscriptionsContract.View,
    OptionsMenuCallbacks {

    override lateinit var coroutineScope: CoroutineScope
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
    private val itemSwipeCallback: ItemSwipeCallback by lazy { ItemSwipeCallback(presenter, subscriptionsAdapter) }

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
            ItemTouchHelper(itemSwipeCallback).attachToRecyclerView(this)
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
                        safeLet(it.topic, it.maxQoS, it.messageType) { topic, qos, messageType ->
                            presenter.saveNewSubscription(
                                topic,
                                qos,
                                messageType
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

    override fun promptToDelete(subscription: SubscriptionViewModel.ActiveSubscription) {
        AlertDialog.Builder(context)
            .setTitle("Delete Subscription")
            .setMessage("Are you sure you want to delete the subscription for ${subscription.topic}?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, _ ->
                presenter.removeSubscription(subscription.topic, subscription.maxQoS, subscription.messageType)
                invalidateOptionsMenu()
                dialog.dismiss()
            }
            .setOnCancelListener {
                dialogCancel(it)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialogCancel(dialog)
            }
            .create()
            .show()
    }

    override fun promptToCancel(subscription: SubscriptionViewModel.NewSubscription) {
        AlertDialog.Builder(context)
            .setTitle("Cancel Subscription Creation")
            .setMessage("Are you sure you want to cancel creating the new subscription?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, _ ->
                subscriptionsAdapter.remove(subscription)
                invalidateOptionsMenu()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialogCancel(dialog)
            }
            .setOnCancelListener {
                dialogCancel(it)
            }
            .create()
            .show()
    }

    override fun showCreateNewSubscription() {
        launch {
            subscriptionsAdapter.add(SubscriptionViewModel.NewSubscription(), 0)
        }
        invalidateOptionsMenu()
    }

    override fun newSubscriptionSaved() {
        launch {
            subscriptionsAdapter.remove(0)
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

    private fun dialogCancel(dialog: DialogInterface) {
        subscriptions.adapter?.notifyDataSetChanged()
        invalidateOptionsMenu()
        dialog.dismiss()
    }

    class ItemSwipeCallback(
        private val presenter: SubscriptionsContract.Presenter,
        private val adapter: SelfBindingRecyclerAdapter<SubscriptionViewModel>
    ) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            adapter.items[viewHolder.adapterPosition]
                .let {
                    when (it) {
                        is SubscriptionViewModel.ActiveSubscription ->
                            presenter.onDeleteSubscription(it)
                        is SubscriptionViewModel.NewSubscription ->
                            presenter.onCancelNewSubscription(it)
                    }
                }
        }

        override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean =
            false
    }
}
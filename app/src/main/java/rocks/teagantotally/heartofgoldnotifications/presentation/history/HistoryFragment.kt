package rocks.teagantotally.heartofgoldnotifications.presentation.history

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.item_message_history.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.ItemBinder
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.SelfBindingRecyclerAdapter
import rocks.teagantotally.heartofgoldnotifications.presentation.history.injection.HistoryModule
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivity
import javax.inject.Inject

@ObsoleteCoroutinesApi
class HistoryFragment : BaseFragment(), HistoryContract.View, Scoped {
    @Inject
    override lateinit var presenter: HistoryContract.Presenter

    override val navigationMenuId: Int = R.id.menu_item_history
    private val receivedAdapter = SelfBindingRecyclerAdapter(MessageItemBinder)
    private val publishedAdapter = SelfBindingRecyclerAdapter(MessageItemBinder)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_history, container, false)

    @UseExperimental(ExperimentalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainActivity.mainActivityComponent
            .historyComponentBuilder()
            .module(HistoryModule(this))
            .build()
            .inject(this)

        with(received_messages) {
            adapter = receivedAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        with(published_messages) {
            adapter = publishedAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        presenter.onViewCreated()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun logMessageReceived(message: Message) {
        launch {
            receivedAdapter.add(message)
        }
    }

    override fun logMessagePublished(message: Message) {
        launch {
            publishedAdapter.add(message)
        }
    }

    override fun showLoading(loading: Boolean) {
        // no-op
    }

    override fun showError(message: String?) {
        // no-op
    }

    object MessageItemBinder : ItemBinder<Message> {
        override fun getLayoutResourceId(item: Message): Int =
            R.layout.item_message_history

        override fun bind(item: Message, view: View) {
            with(view) {
                payload.text = item.payload
            }
        }
    }
}
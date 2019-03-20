package rocks.teagantotally.heartofgoldnotifications.presentation.history

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.item_message_history.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import org.json.JSONObject
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import rocks.teagantotally.heartofgoldnotifications.presentation.common.annotations.ActionBarTitle
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.ItemBinder
import rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview.SelfBindingRecyclerAdapter
import rocks.teagantotally.heartofgoldnotifications.presentation.history.injection.HistoryModule
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivity
import java.text.SimpleDateFormat
import javax.inject.Inject

@ObsoleteCoroutinesApi
@ActionBarTitle(R.string.title_message_history)
class HistoryFragment : BaseFragment(), HistoryContract.View, Scoped {
    companion object {
        private val DATE_FORMATTER = SimpleDateFormat.getDateTimeInstance()
    }

    @Inject
    override lateinit var presenter: HistoryContract.Presenter

    override val navigationMenuId: Int = R.id.menu_item_history
    private val receivedAdapter = SelfBindingRecyclerAdapter(MessageItemBinder)
    private val publishedAdapter = SelfBindingRecyclerAdapter(MessageItemBinder)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        with(received_messages_label) {
            setOnClickListener {
                when (received_messages.visibility == View.VISIBLE) {
                    true ->
                        setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_down,
                            0
                        ).run { received_messages.visibility = View.GONE }
                    false ->
                        setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_up,
                            0
                        ).run { received_messages.visibility = View.VISIBLE }
                }
            }

        }
        with(received_messages) {
            adapter = receivedAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        with(published_messages_label) {
            setOnClickListener {
                when (published_messages.visibility == View.VISIBLE) {
                    true ->
                        setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_down,
                            0
                        ).run { published_messages.visibility = View.GONE }
                    false ->
                        setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_arrow_up,
                            0
                        ).run { published_messages.visibility = View.VISIBLE }
                }
            }

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


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_history, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu
            ?.findItem(R.id.menu_item_delete_history)
            ?.icon
            ?.setTint(Color.WHITE)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        when (item?.itemId) {
            R.id.menu_item_delete_history ->
                presenter.onDeleteHistory()
            else -> null
        }?.run { true }
            ?: false

    override fun logMessageReceived(message: Message) {
        launch {
            receivedAdapter.add(message)
//            with(received_messages_scrollview) {
//                post {
//                    scrollTo(0, bottom)
//                }
//            }
        }
    }

    override fun logMessagePublished(message: Message) {
        launch {
            publishedAdapter.add(message)
//            with(published_messages_scrollview) {
//                post {
//                    scrollTo(0, bottom)
//                }
//            }
        }
    }

    override fun clearHistory() {
        launch {
            receivedAdapter.clear()
            publishedAdapter.clear()
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
                timestamp.text = DATE_FORMATTER.format(item.date)
                payload.text = try {
                    JSONObject(item.payload).toString(2)
                } catch (_: Throwable) {
                    item.payload
                }
                topic.text = item.topic
            }
        }
    }

}
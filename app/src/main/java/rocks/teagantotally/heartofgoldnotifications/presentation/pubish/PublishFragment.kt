package rocks.teagantotally.heartofgoldnotifications.presentation.pubish

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.*
import kotlinx.android.synthetic.main.fragment_publish.*
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.common.extensions.asQoS
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import rocks.teagantotally.heartofgoldnotifications.presentation.common.OptionsMenuCallbacks
import rocks.teagantotally.heartofgoldnotifications.presentation.common.SimpleTextWatcher
import rocks.teagantotally.heartofgoldnotifications.presentation.common.annotations.ActionBarTitle
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.pubish.injection.PublishModule
import javax.inject.Inject

@ActionBarTitle(R.string.title_publish)
class PublishFragment : BaseFragment(), Scoped, PublishContract.View, OptionsMenuCallbacks {
    @Inject
    override lateinit var presenter: PublishContract.Presenter

    override val navigationMenuId: Int = R.id.menu_item_publish
    override var isValid: Boolean = false
        set(value) {
            field = value
            activity?.invalidateOptionsMenu()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_publish, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainActivity.mainActivityComponent
            .publishComponentBuilder()
            .module(PublishModule(this))
            .build()
            .inject(this)

        with(TextWatcher(this)) {
            publish_topic.addTextChangedListener(this)
            publish_payload.addTextChangedListener(this)
        }

        publish_qos.asQoS()

        presenter.onViewCreated()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_publish, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.findItem(R.id.menu_item_publish)
            ?.let {
                it.isEnabled = isValid
                with(it.icon) {
                    setTint(Color.WHITE)
                    alpha =
                        when (isValid) {
                            true -> 255
                            false -> 127
                        }
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        when (item?.itemId) {
            R.id.menu_item_publish ->
                presenter.publish(
                    publish_topic.text!!.toString(),
                    publish_payload.text!!.toString(),
                    publish_retain.isChecked,
                    publish_qos.selectedItem as? Int ?: 0
                )
            else -> null
        }
            ?.run { true }
            ?: false

    override fun showSuccess() {

    }

    override fun showLoading(loading: Boolean) {

    }

    override fun showError(message: String?) {

    }

    override fun invalidateOptionsMenu() {
        presenter.checkValid(
            publish_topic.text?.toString(),
            publish_payload.text?.toString()
        )
    }

    class TextWatcher(
        private val optionsMenuCallbacks: OptionsMenuCallbacks
    ) : SimpleTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            optionsMenuCallbacks.invalidateOptionsMenu()
        }
    }
}
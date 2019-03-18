package rocks.teagantotally.heartofgoldnotifications.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrueMaybe

abstract class SimpleTypedSpinner<ItemType> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Spinner(
    context,
    attrs,
    defStyleAttr
), AdapterView.OnItemSelectedListener {
    private lateinit var options: Array<String>
    private var selectedListener: OnItemSelectedListener<ItemType>? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        options = getOptions()

        adapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_item,
            options
        )

        onItemSelectedListener = this
    }

    // Force use of custom listener
    final override fun setOnItemSelectedListener(listener: AdapterView.OnItemSelectedListener?) {
        super.setOnItemSelectedListener(listener)
    }

    final override fun setSelection(position: Int) {
        setSelection(position, true)
    }

    final override fun setSelection(position: Int, animate: Boolean) {
        super.setSelection(position, animate)
    }

    fun selectItem(item: ItemType?) {
        item
            ?.let {
                options.indexOf(convert(item))
                    .let {
                        setSelection(it, true)
                    }
            }?.run { setSelection(0, true) }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedListener?.onItemSelected(null)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedListener
            ?.let { listener ->
                (position in 0..(adapter.count - 1))
                    .ifTrueMaybe {
                        (adapter as? ArrayAdapter<String>)
                            ?.let { it.getItem(position) }
                            ?.let { parse(it) }
                            ?.let { listener.onItemSelected(it) }
                    }
            } ?: onNothingSelected(parent)
    }

    fun setItemSelectedListener(listener: OnItemSelectedListener<ItemType>?) {
        selectedListener = listener
    }

    fun getOnItemSelected(): OnItemSelectedListener<ItemType>? = selectedListener

    protected abstract fun getOptions(): Array<String>

    protected abstract fun parse(stringItem: String): ItemType?

    protected abstract fun convert(item: ItemType): String

    interface OnItemSelectedListener<ItemType> {
        fun onItemSelected(item: ItemType?)
    }
}
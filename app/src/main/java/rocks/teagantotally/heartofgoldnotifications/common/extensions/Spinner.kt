package rocks.teagantotally.heartofgoldnotifications.common.extensions

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType

fun Spinner.asMessageType(itemSelectedListener: (MessageType?) -> Unit) {
    adapter = ArrayAdapter<String>(
        context,
        android.R.layout.simple_spinner_item,
        MessageType.values()
            .map { it.name }
            .toTypedArray()
    )
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            itemSelectedListener(null)
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            adapter.getItem(position)
                ?.let { it as? String }
                ?.let { MessageType.valueOf(it) }
                .let { itemSelectedListener(it) }
        }
    }
}

fun Spinner.asQoS(itemSelectedListener: ((Int?) -> Unit)? = null) {
    adapter = ArrayAdapter<String>(
        context,
        android.R.layout.simple_spinner_item,
        context.resources
            .getIntArray(R.array.mqtt_qos)
            .map { it.toString() }
            .toTypedArray()
    )
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            itemSelectedListener?.invoke(null)
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            adapter.getItem(position)
                ?.let { it as? String }
                ?.let { it.toIntOrNull() }
                .let { itemSelectedListener?.invoke(it) }
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <ItemType> Spinner.select(item: ItemType?, comparator: (ItemType?, String?) -> Boolean) {
    for (i in 0..(adapter.count - 1)) {
        comparator(item, adapter.getItem(i) as? String)
            .ifTrue {
                setSelection(i, true)
                return
            }
    }
}
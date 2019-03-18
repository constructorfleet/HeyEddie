package rocks.teagantotally.heartofgoldnotifications.presentation.views

import android.content.Context
import android.util.AttributeSet
import rocks.teagantotally.heartofgoldnotifications.R

class QoSSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SimpleTypedSpinner<Int>(
    context,
    attrs,
    defStyleAttr
) {
    override fun getOptions(): Array<String> =
        context.resources
            .getIntArray(R.array.mqtt_qos)
            .map { it.toString() }
            .toTypedArray()

    override fun parse(stringItem: String): Int? =
        stringItem.toIntOrNull()


    override fun convert(item: Int): String =
            item.toString()
}
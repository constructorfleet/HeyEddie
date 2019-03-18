package rocks.teagantotally.heartofgoldnotifications.presentation.views

import android.content.Context
import android.util.AttributeSet
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.MessageType

class MessageTypeSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SimpleTypedSpinner<MessageType>(
    context,
    attrs,
    defStyleAttr
) {
    override fun getOptions(): Array<String> =
        MessageType.values()
            .map { it.name }
            .toTypedArray()

    override fun parse(stringItem: String): MessageType? =
        MessageType.valueOf(stringItem)

    override fun convert(item: MessageType): String =
        item.name
}
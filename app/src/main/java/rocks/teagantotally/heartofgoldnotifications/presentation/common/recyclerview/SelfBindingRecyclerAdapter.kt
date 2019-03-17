package rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrue


@Suppress("UNCHECKED_CAST")
class SelfBindingRecyclerAdapter<ItemType>(
    private val itemBinder: ItemBinder<ItemType>,
    val items: MutableList<ItemType> = mutableListOf(),
    var itemClickListener: ItemClickListener<ItemType>? = null
) : RecyclerView.Adapter<SelfBindingRecyclerAdapter.ViewHolder>(), View.OnClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, layoutId: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView
        val item = items[position]

        view.setTag(TAG_ITEM_MODEL, item)
        if (itemClickListener != null) {
            view.setOnClickListener {
                itemClickListener?.onClick(item)
            }
        }

        itemBinder.bind(item, view)
    }

    fun add(item: ItemType) {
        items.add(item)
        notifyItemInserted(items.lastIndex)
    }

    fun add(
        item: ItemType,
        index: Int
    ) {
        items.add(index, item)
        notifyItemInserted(index)
    }

    fun addAll(itemsToAdd: Collection<ItemType>) {
        items.lastIndex
            .let {
                items.addAll(itemsToAdd)
                notifyItemRangeInserted(it, itemsToAdd.size)
            }
    }

    fun remove(item: ItemType) {
        items.indexOf(item)
            .ifTrue({ it >= 0 }) {
                items.removeAt(it)
                notifyItemRemoved(it)
            }
    }

    fun remove(index: Int) {
        if (index in 0..items.lastIndex) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun removeAll(items: Collection<ItemType>) {
        for (item in items) {
            remove(item)
        }
    }

    fun updateItem(
        index: Int,
        newItem: ItemType
    ) {
        if (index in 0..items.lastIndex) {
            items.removeAt(index)
            items.add(index, newItem)
            notifyItemChanged(index)
        }
    }

    fun moveItem(
        fromIndex: Int,
        toIndex: Int
    ) {
        if (fromIndex in 0..items.lastIndex) {
            items.removeAt(fromIndex)
                .let {
                    items.add(toIndex, it)
                    notifyItemMoved(fromIndex, toIndex)
                }
        }
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        itemBinder.getLayoutResourceId(items[position])

    override fun getItemCount(): Int =
        items.size

    override fun onClick(view: View) {
        (view.getTag(TAG_ITEM_MODEL) as? ItemType)
            ?.let { itemClickListener?.onClick(it) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private const val TAG_ITEM_MODEL = -124
    }
}
package rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import lk.kotlin.observable.list.ObservableList
import lk.kotlin.observable.list.observableListOf


@Suppress("UNCHECKED_CAST")
class SelfBindingRecyclerAdapter<ItemType>(
    private val itemBinder: ItemBinder<ItemType>,
    val items: ObservableList<ItemType> = observableListOf(),
    var itemClickListener: ItemClickListener<ItemType>? = null
) : RecyclerView.Adapter<SelfBindingRecyclerAdapter.ViewHolder>(), View.OnClickListener {
    private lateinit var inflater: LayoutInflater

    init {
        items.onAdd += { _, position ->
            notifyItemInserted(position)
        }
        items.onChange += { _, _, position ->
            notifyItemChanged(position)
        }
        items.onMove += { _, oldPosition, newPosition ->
            notifyItemMoved(oldPosition, newPosition)
        }
        items.onRemove += { _, position ->
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, layoutId: Int): ViewHolder {
        if (!this::inflater.isInitialized) {
            inflater = LayoutInflater.from(parent.context)
        }

        val view = inflater.inflate(layoutId, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView
        val item = items[position]

        view.tag = item
        if (itemClickListener != null) {
            view.setOnClickListener {
                itemClickListener?.onClick(item)
            }
        }

        itemBinder.bind(item, view)
    }

    fun add(item: ItemType) {
        items.add(item)
    }

    fun add(
        item: ItemType,
        index: Int
    ) {
        items.add(index, item)
    }

    fun addAll(itemsToAdd: Collection<ItemType>) {
        val index = items.lastIndex
        items.addAll(itemsToAdd)
    }

    fun remove(item: ItemType) {
        val index = items.indexOf(item)
        if (index < 0) {
            return
        }

        items.removeAt(index)
    }

    fun remove(index: Int) {
        items.removeAt(index)
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
        items.removeAt(index)
        items.add(index, newItem)
    }

    fun moveItem(
        fromIndex: Int,
        toIndex: Int
    ) {
        items.move(fromIndex, toIndex)
    }

    override fun getItemViewType(position: Int): Int =
        itemBinder.getLayoutResourceId(items[position])

    override fun getItemCount(): Int =
        items.size

    override fun onClick(view: View) {
        (view.tag as? ItemType)
            ?.let { itemClickListener?.onClick(it) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private const val ITEM_MODEL = -124
    }
}
package rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview

import android.view.View

class CompositeItemBinder<ItemType>(
    vararg val binders: ConditionalItemBinder<ItemType>
) : ItemBinder<ItemType> {
    override fun getLayoutResourceId(item: ItemType): Int =
        getBinderFor(item)?.getLayoutResourceId(item) ?: 0

    override fun bind(item: ItemType, view: View) {
        getBinderFor(item)?.bind(item, view)
    }

    private fun getBinderFor(item: ItemType): ConditionalItemBinder<ItemType>? =
        binders
            .firstOrNull { it.canBind(item) }
}
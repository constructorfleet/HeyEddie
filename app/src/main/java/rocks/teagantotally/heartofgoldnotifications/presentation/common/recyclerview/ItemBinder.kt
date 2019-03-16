package rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview

import android.view.View

interface ItemBinder<ItemType> {
    fun getLayoutResourceId(item: ItemType): Int

    fun bind(item: ItemType, view: View)
}
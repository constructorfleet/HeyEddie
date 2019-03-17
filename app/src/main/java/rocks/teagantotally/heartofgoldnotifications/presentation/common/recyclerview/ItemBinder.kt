package rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview

import android.support.annotation.LayoutRes
import android.view.View

interface ItemBinder<ItemType> {
    @LayoutRes
    fun getLayoutResourceId(item: ItemType): Int

    fun bind(item: ItemType, view: View)
}
package rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview

interface ItemClickListener<ItemType> {
    fun onClick(item: ItemType)
}
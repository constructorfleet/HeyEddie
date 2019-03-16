package rocks.teagantotally.heartofgoldnotifications.presentation.common.recyclerview

interface ConditionalItemBinder<ItemType> : ItemBinder<ItemType> {
    fun canBind(item: ItemType): Boolean
}
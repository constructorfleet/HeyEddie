package rocks.teagantotally.heartofgoldnotifications.app.injection

sealed class SubComponent<ComponentType> {
    class NotInitialized<ComponentType> : SubComponent<ComponentType>()
    class Initialized<ComponentType>(val component: ComponentType) : SubComponent<ComponentType>()
}
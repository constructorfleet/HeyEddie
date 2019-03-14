package rocks.teagantotally.heartofgoldnotifications.domain.models.events

interface Event

interface Failure<Type> : Event {
    val source: Type
    val throwable: Throwable?
}

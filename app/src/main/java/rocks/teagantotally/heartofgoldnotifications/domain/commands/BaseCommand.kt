package rocks.teagantotally.heartofgoldnotifications.domain.commands

abstract class BaseCommand<EventType> {
    abstract operator fun invoke(command: EventType)
}
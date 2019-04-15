package rocks.teagantotally.heartofgoldnotifications.domain.models.events

abstract class ConfigurationChangedEvent<ConfigurationType>(
    val old: ConfigurationType?,
    val new: ConfigurationType
) : Event {
}
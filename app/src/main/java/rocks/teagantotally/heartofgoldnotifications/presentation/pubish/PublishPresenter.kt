package rocks.teagantotally.heartofgoldnotifications.presentation.pubish

import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.domain.framework.event.MqttEventConsumer
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.events.MqttEvent
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.publish.PublishMessage
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter
import java.util.*

class PublishPresenter(
    view: PublishContract.View,
    private val publishMessage: PublishMessage
) : PublishContract.Presenter, ScopedPresenter<PublishContract.View, PublishContract.Presenter>(view),
    MqttEventConsumer {
    private var messagePublishing: Message? = null
    private var commandPublishing: MqttCommand.Publish? = null

    override fun onViewCreated() {
        view.isValid = false
    }

    override fun checkValid(topic: String?, payload: String?) {
        view.isValid = (!topic.isNullOrBlank() && !payload.isNullOrBlank())
    }

    override fun publish(topic: String, payload: String, retain: Boolean, qos: Int) {
        launch {
            view.showLoading(true)
            publishMessage(
                MqttCommand.Publish(
                    Message(
                        topic,
                        payload,
                        qos,
                        retain,
                        Date()
                    ).also { messagePublishing = it }
                ).also { commandPublishing = it }
            )
        }
    }

    override fun consume(event: MqttEvent) {
        when (event) {
            is MqttEvent.MessagePublished ->
                if (event.message == messagePublishing) {
                    view.showSuccess()
                } else {
                    null
                }
            is MqttEvent.CommandFailed ->
                if (event.command == commandPublishing) {
                    view.showError(event.throwable.message)
                } else {
                    null
                }
            else -> null
        }
            ?.run { view.showLoading(false) }
            ?: view.showError("Error Processing Message")
    }

    override fun onDestroyView() {

    }
}

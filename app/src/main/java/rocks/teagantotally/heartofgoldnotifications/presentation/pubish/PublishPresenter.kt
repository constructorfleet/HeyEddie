package rocks.teagantotally.heartofgoldnotifications.presentation.pubish

import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.domain.models.commands.MqttCommand
import rocks.teagantotally.heartofgoldnotifications.domain.models.messages.Message
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.message.publish.PublishMessage
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter
import java.util.*

class PublishPresenter(
    view: PublishContract.View,
    private val publishMessage: PublishMessage
) : PublishContract.Presenter, ScopedPresenter<PublishContract.View, PublishContract.Presenter>(view) {
    override fun onViewCreated() {
        view.isValid = false
    }

    override fun checkValid(topic: String?, payload: String?) {
        view.isValid = (!topic.isNullOrBlank() && !payload.isNullOrBlank())
    }

    override fun publish(topic: String, payload: String, retain: Boolean, qos: Int) {
        launch {
            publishMessage(
                MqttCommand.Publish(
                    Message(
                        topic,
                        payload,
                        qos,
                        retain,
                        Date()
                    )
                )
            )
        }
    }

    override fun onDestroyView() {

    }
}
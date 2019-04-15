package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.pubish

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.app.injection.client.ClientContainer
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifMaybe
import rocks.teagantotally.heartofgoldnotifications.domain.usecases.mqtt.message.publish.PublishMessage
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ScopedPresenter
import rocks.teagantotally.kotqtt.domain.framework.client.CommandResult
import rocks.teagantotally.kotqtt.domain.framework.client.MqttEventProducer
import rocks.teagantotally.kotqtt.domain.models.Message
import rocks.teagantotally.kotqtt.domain.models.QoS
import rocks.teagantotally.kotqtt.domain.models.commands.MqttPublishCommand
import java.util.*

class PublishPresenter(
    view: PublishContract.View,
    coroutineScope: CoroutineScope
) : PublishContract.Presenter, ScopedPresenter<PublishContract.View, PublishContract.Presenter>(view, coroutineScope) {

    private var messagePublishing: Message? = null

    private val clientContainer: ClientContainer?
        get() = HeyEddieApplication.clientComponent?.provideClientContainer()

    private val eventProducer: MqttEventProducer?
        get() = clientContainer?.eventProducer
    private val publishMessage: PublishMessage?
        get() = clientContainer?.publishMessage

    override fun onViewCreated() {
        view.isValid = false
    }

    override fun checkValid(topic: String?, payload: String?) {
        view.isValid = (!topic.isNullOrBlank() && !payload.isNullOrBlank())
    }

    override fun publish(topic: String, payload: String, retain: Boolean, qos: Int) {
        launch {
            view.showLoading(true)
            publishMessage?.invoke(
                MqttPublishCommand(
                    Message(
                        topic,
                        retain,
                        QoS.fromQoS(qos),
                        payload.toByteArray(),
                        Date()
                    ).also { messagePublishing = it }
                )
            )
            eventProducer?.subscribe()
                ?.run {
                    while (!isClosedForReceive) {
                        consumeEach { event ->
                            (event as? CommandResult<*>)
                                ?.ifMaybe({ it.command is MqttPublishCommand }) {
                                    when (it) {
                                        is CommandResult.Success<*, *> ->
                                            view.showSuccess()
                                        is CommandResult.Failure<*> -> view.showError(it.throwable.message)
                                    }
                                }
                                ?.let { cancel() }
                                ?.run { view.showLoading(false) }
                        }
                    }
                }
        }
    }

    override fun onDestroyView() {

    }
}

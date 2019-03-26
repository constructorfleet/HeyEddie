package rocks.teagantotally.kotqtt.domain.models

enum class QoS(val value: Int) {
    AT_MOST_ONCE(0),
    AT_LEAST_ONCE(1),
    EXACTLY_ONCE(2);

    companion object {
        val DEFAULT_QOS = AT_MOST_ONCE

        fun fromQoS(qos: Int): QoS =
            QoS.values().firstOrNull { it.value == qos } ?: DEFAULT_QOS
    }
}
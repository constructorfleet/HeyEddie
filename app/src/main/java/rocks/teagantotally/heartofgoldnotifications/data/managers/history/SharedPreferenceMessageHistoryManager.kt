package rocks.teagantotally.heartofgoldnotifications.data.managers.history

import android.content.SharedPreferences
import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.MessageHistoryManager
import rocks.teagantotally.kotqtt.domain.models.Message

class SharedPreferenceMessageHistoryManager(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : MessageHistoryManager {

    private val listeners: MutableSet<MessageHistoryManager.Listener> = mutableSetOf()

    companion object {
        private const val KEY_RECEIVED_MESSAGES = "received_messages"
        private const val KEY_PUBLISHED_MESSAGES = "published_messages"
    }

    override fun addListener(listener: MessageHistoryManager.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: MessageHistoryManager.Listener) {
        listeners.remove(listener)
    }

    override fun recordMessageReceived(message: Message) {
        serializeMessage(message)
            ?.let { serializedMessage ->
                sharedPreferences.getStringSet(KEY_RECEIVED_MESSAGES, mutableSetOf())
                    ?.apply { add(serializedMessage) }
                    ?.let {
                        sharedPreferences
                            .edit()
                            .putStringSet(KEY_RECEIVED_MESSAGES, it)
                            .apply()
                    }
                    ?.run {
                        listeners
                            .forEach { it.onMessageReceived(message) }
                    }
            } ?: Timber.e { "Unable to record received message" }
    }

    override fun recordMessagePublished(message: Message) {
        serializeMessage(message)
            ?.let { serializedMessage ->
                sharedPreferences.getStringSet(KEY_PUBLISHED_MESSAGES, mutableSetOf())
                    ?.apply { add(serializedMessage) }
                    ?.let {
                        sharedPreferences
                            .edit()
                            .putStringSet(KEY_PUBLISHED_MESSAGES, it)
                            .apply()
                    }
                    ?.run {
                        listeners
                            .forEach { it.onMessagePublished(message) }
                    }
            } ?: Timber.e { "Unable to record received message" }
    }

    override fun getReceivedMessages(): List<Message> =
        sharedPreferences.getStringSet(KEY_RECEIVED_MESSAGES, mutableSetOf())
            ?.mapNotNull { deserializeMessage(it) }
            ?.sortedBy { it.date }
            ?: emptyList()

    override fun getPublishedMessages(): List<Message> =
        sharedPreferences.getStringSet(KEY_PUBLISHED_MESSAGES, mutableSetOf())
            ?.mapNotNull { deserializeMessage(it) }
            ?.sortedBy { it.date }
            ?: emptyList()

    override fun clear() {
        sharedPreferences
            .edit()
            .putStringSet(KEY_RECEIVED_MESSAGES, mutableSetOf())
            .putStringSet(KEY_PUBLISHED_MESSAGES, mutableSetOf())
            .apply()
    }

    private fun serializeMessage(message: Message): String? =
        try {
            gson.toJson(message)
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            null
        }

    private fun deserializeMessage(serializedMessage: String): Message? =
        try {
            gson.fromJson(serializedMessage, Message::class.java)
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            null
        }
}
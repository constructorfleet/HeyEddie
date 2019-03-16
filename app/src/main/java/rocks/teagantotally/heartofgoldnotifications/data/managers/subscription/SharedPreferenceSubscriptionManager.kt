package rocks.teagantotally.heartofgoldnotifications.data.managers.subscription

import android.content.SharedPreferences
import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import rocks.teagantotally.heartofgoldnotifications.common.extensions.ifTrueAlso
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.SubscriptionManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.SubscriptionConfiguration

class SharedPreferenceSubscriptionManager(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SubscriptionManager {

    companion object {
        private const val KEY_SUBSCRIPTIONS = "subscriptions"
    }

    private val listeners: MutableSet<SubscriptionManager.Listener> = mutableSetOf()

    override fun addListener(listener: SubscriptionManager.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: SubscriptionManager.Listener) {
        listeners.remove(listener)
    }

    override fun addSubscription(subscription: SubscriptionConfiguration) {
        serializeSubscription(subscription)
            ?.let { serializeSubscription ->
                sharedPreferences.getStringSet(KEY_SUBSCRIPTIONS, mutableSetOf())
                    ?.apply { add(serializeSubscription) }
                    .let {
                        sharedPreferences
                            .edit().putStringSet(KEY_SUBSCRIPTIONS, it)
                            .apply()
                    }
                    .run {
                        listeners
                            .forEach { it.onSubcriptionAdded(subscription) }
                    }
            }
    }

    override fun removeSubscription(subscription: SubscriptionConfiguration) {
        sharedPreferences.getStringSet(KEY_SUBSCRIPTIONS, mutableSetOf())
            ?.apply {
                removeIf {
                    deserializeSubscription(it)
                        ?.let { it.topic == subscription.topic }
                        ?.ifTrueAlso { listeners.forEach { it.onSubscriptionRemoved(subscription) } }
                        ?: false
                }

            }
            .let {
                sharedPreferences
                    .edit().putStringSet(KEY_SUBSCRIPTIONS, it)
                    .apply()
            }

    }

    override fun getSubscriptions(): Set<SubscriptionConfiguration> =
        sharedPreferences.getStringSet(KEY_SUBSCRIPTIONS, emptySet())
            ?.mapNotNull { deserializeSubscription(it) }
            ?.toSet() ?: emptySet()

    override fun getSubscriptionsFor(topic: String): Set<SubscriptionConfiguration> =
        sharedPreferences.getStringSet(KEY_SUBSCRIPTIONS, emptySet())
            ?.mapNotNull { deserializeSubscription(it) }
            ?.filterNot { it.topic == topic }
            ?.toSet() ?: emptySet()

    private fun serializeSubscription(subscription: SubscriptionConfiguration): String? =
        try {
            gson.toJson(subscription)
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            null
        }

    private fun deserializeSubscription(serializedSubscription: String): SubscriptionConfiguration? =
        try {
            gson.fromJson(serializedSubscription, SubscriptionConfiguration::class.java)
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            null
        }
}
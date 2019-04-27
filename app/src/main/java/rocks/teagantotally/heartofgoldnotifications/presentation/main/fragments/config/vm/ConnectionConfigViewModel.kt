package rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.vm

import android.support.v14.preference.SwitchPreference
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.PreferenceFragmentCompat
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.common.extensions.safeLet
import rocks.teagantotally.heartofgoldnotifications.domain.framework.managers.ConnectionConfigManager
import rocks.teagantotally.heartofgoldnotifications.domain.models.configs.ConnectionConfiguration
import kotlin.reflect.KClass

class ConnectionConfigViewModel(
    private val connectionConfigManager: ConnectionConfigManager,
    fragment: PreferenceFragmentCompat
) : ConfigViewModel<ConnectionConfiguration>(fragment) {
    override val preferenceTypeMap: Map<Int, KClass<*>> =
        mapOf(
            Pair(R.string.pref_broker_host, EditTextPreference::class),
            Pair(R.string.pref_broker_port, EditTextPreference::class),
            Pair(R.string.pref_username, EditTextPreference::class),
            Pair(R.string.pref_password, EditTextPreference::class),
            Pair(R.string.pref_client_id, EditTextPreference::class),
            Pair(R.string.pref_reconnect, SwitchPreference::class),
            Pair(R.string.pref_clean_session, SwitchPreference::class)
        )

    override fun populate() {
        retrieve()
            ?.run {
                setValue(R.string.pref_broker_host, brokerHost)
                setValue(R.string.pref_broker_port, brokerPort)
                setValue(R.string.pref_username, clientUsername)
                setValue(R.string.pref_password, clientPassword)
                setValue(R.string.pref_client_id, clientId)
                setValue(R.string.pref_reconnect, autoReconnect)
                setValue(R.string.pref_clean_session, cleanSession)
            }
    }

    override suspend fun save() {
        retrieve()
            ?.let { connectionConfigManager.saveConfiguration(it) }
    }

    override fun isValid(): Boolean = retrieve() != null

    override fun retrieve(): ConnectionConfiguration? =
        safeLet(
            getValue(R.string.pref_broker_host, String::class),
            getValue(R.string.pref_broker_port, String::class),
            getValue(R.string.pref_username, String::class),
            getValue(R.string.pref_password, String::class),
            getValue(R.string.pref_client_id, String::class)
        ) { host, port, username, password, clientId ->
            ConnectionConfiguration(
                host,
                port.toInt(),
                username,
                password,
                clientId,
                getValue(R.string.pref_reconnect, Boolean::class) ?: false,
                getValue(R.string.pref_clean_session, Boolean::class) ?: false
            )
        }
}
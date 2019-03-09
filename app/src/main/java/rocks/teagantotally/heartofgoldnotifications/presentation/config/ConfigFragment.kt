package rocks.teagantotally.heartofgoldnotifications.presentation.config

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SwitchPreferenceCompat
import kotlinx.coroutines.CoroutineScope
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.presentation.MainActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.config.injection.ConfigModule
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class ConfigFragment : PreferenceFragmentCompat(), ConfigContract.View,
    SharedPreferences.OnSharedPreferenceChangeListener, CoroutineScope {
    @Inject
    override lateinit var presenter: ConfigContract.Presenter
    @Inject
    override lateinit var coroutineContext: CoroutineContext

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        MainActivity.mainActivityComponent
            .configComponentBuilder()
            .module(ConfigModule(this))
            .build()
            .inject(this)

        setPreferencesFromResource(R.xml.connection_configuration, rootKey)

        presenter.onViewCreated()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        findPreference(key)
            ?.let { it as? EditTextPreference }
            ?.let {
                when (key == getString(R.string.pref_password)) {
                    false -> it.summary = it.text
                    true -> it.summary = String(CharArray(it.text.length) { '*' })
                }
            }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun setHost(host: String) {
        findPreference(getString(R.string.pref_broker_host))
            ?.let { it as? EditTextPreference }
            ?.let {
                it.text = host
                it.summary = host
            }
    }

    override fun setPort(port: Int) {
        findPreference(getString(R.string.pref_broker_port))
            ?.let { it as? EditTextPreference }
            ?.let {
                it.text = port.toString()
                it.summary = port.toString()
            }
    }

    override fun setUsername(usernmae: String) {
        findPreference(getString(R.string.pref_username))
            ?.let { it as? EditTextPreference }
            ?.let {
                it.text = usernmae
                it.summary = usernmae
            }
    }

    override fun setPassword(password: String) {
        findPreference(getString(R.string.pref_password))
            ?.let { it as? EditTextPreference }
            ?.let {
                it.text = password
                it.summary = String(CharArray(password.length) { '*' })
            }
    }

    override fun setClientId(clientId: String) {
        findPreference(getString(R.string.pref_client_id))
            ?.let { it as? EditTextPreference }
            ?.let {
                it.summary = clientId
                it.text = clientId
            }
    }

    override fun setReconnect(reconnect: Boolean) {
        findPreference(getString(R.string.pref_reconnect))
            ?.let { it as? SwitchPreferenceCompat }
            ?.let { it.isChecked = reconnect }
    }

    override fun setCleanSession(cleanSession: Boolean) {
        findPreference(getString(R.string.pref_clean_session))
            ?.let { it as? SwitchPreferenceCompat }
            ?.let { it.isChecked = cleanSession }
    }


    override fun showLoading(loading: Boolean) {
        // no-op
    }

    override fun showError() {
        // no-op
    }
}
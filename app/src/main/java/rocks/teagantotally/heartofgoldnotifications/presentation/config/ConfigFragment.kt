package rocks.teagantotally.heartofgoldnotifications.presentation.config

import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v14.preference.SwitchPreference
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.common.extensions.safeLet
import rocks.teagantotally.heartofgoldnotifications.presentation.main.MainActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Navigable
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped
import rocks.teagantotally.heartofgoldnotifications.presentation.common.annotations.ActionBarTitle
import rocks.teagantotally.heartofgoldnotifications.presentation.config.injection.ConfigModule
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@ActionBarTitle(R.string.title_connection_config)
class ConfigFragment : PreferenceFragmentCompat(), ConfigContract.View,
    SharedPreferences.OnSharedPreferenceChangeListener, Navigable, Scoped {

    companion object {
        const val TAG = "rocks.teagantotally.heartofgoldnotifications.presentation.config.ConfigFragment"
    }

    override lateinit var job: Job
    override val coroutineContext: CoroutineContext by lazy { job.plus(Dispatchers.Main) }

    @Inject
    override lateinit var presenter: ConfigContract.Presenter

    override val navigationMenuId: Int = R.id.menu_item_settings
    override var isValid: Boolean = false
        set(value) {
            field = value
            activity?.invalidateOptionsMenu()
        }

    private val brokerHostPreference: EditTextPreference?
        get() =
            findPreference(getString(R.string.pref_broker_host))
                ?.let { it as? EditTextPreference }
    private val brokerPortPreference: EditTextPreference?
        get() =
            findPreference(getString(R.string.pref_broker_port))
                ?.let { it as? EditTextPreference }
    private val usernamePreference: EditTextPreference?
        get() =
            findPreference(getString(R.string.pref_username))
                ?.let { it as? EditTextPreference }
    private val passwordPreference: EditTextPreference?
        get() =
            findPreference(getString(R.string.pref_password))
                ?.let { it as? EditTextPreference }
    private val clientIdPreference: EditTextPreference?
        get() =
            findPreference(getString(R.string.pref_client_id))
                ?.let { it as? EditTextPreference }
    private val reconnectPreference: SwitchPreference?
        get() =
            findPreference(getString(R.string.pref_reconnect))
                ?.let { it as? SwitchPreference }
    private val cleanSessionPreference: SwitchPreference?
        get() =
            findPreference(getString(R.string.pref_clean_session))
                ?.let { it as? SwitchPreference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.connection_configuration, rootKey)

        MainActivity.mainActivityComponent
            .configComponentBuilder()
            .module(ConfigModule(this))
            .build()
            .inject(this)

        presenter.onViewCreated()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_save, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        menu?.findItem(R.id.menu_item_save)
            ?.let {
                it.isEnabled = isValid
                it.icon.alpha =
                    when (isValid) {
                        true -> 255
                        false -> 127
                    }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        when (item?.itemId) {
            R.id.menu_item_save ->
                try {
                    presenter.saveConfig(
                        brokerHostPreference!!.text,
                        brokerPortPreference!!.text.toInt(),
                        usernamePreference?.text,
                        passwordPreference?.text,
                        clientIdPreference!!.text,
                        reconnectPreference!!.isChecked,
                        cleanSessionPreference!!.isChecked
                    )
                } catch (_: Throwable) {
                    showError("Something went wrong")
                }.run { true }
            else -> false
        }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        setSummary(key)
            .run { checkValidity() }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            .let { preferences ->
                preferences.registerOnSharedPreferenceChangeListener(this)
                preferences.all
                    .map { it.key }
                    .forEach { prefKey ->
                        setSummary(prefKey)
                    }
            }
        checkValidity()
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun setHost(host: String) {
        brokerHostPreference
            ?.text = host
    }

    override fun setPort(port: Int) {
        brokerPortPreference
            ?.text = port.toString()
    }

    override fun setUsername(usernmae: String?) {
        usernamePreference
            ?.text = usernmae
    }

    override fun setPassword(password: String?) {
        passwordPreference
            ?.text = password
    }

    override fun setClientId(clientId: String) {
        clientIdPreference
            ?.text = clientId
    }

    override fun setReconnect(reconnect: Boolean) {
        reconnectPreference
            ?.isChecked = reconnect
    }

    override fun setCleanSession(cleanSession: Boolean) {
        cleanSessionPreference
            ?.isChecked = cleanSession
    }

    override fun showLoading(loading: Boolean) {
        // no-op
    }

    override fun showError(message: String?) {
        safeLet(message, view) { msg, view ->
            Snackbar.make(
                view,
                msg,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun close() {
        activity?.onBackPressed()
    }

    private fun setSummary(key: String?) =
        findPreference(key)
            ?.let { it as? EditTextPreference }
            ?.let {
                when (key == getString(R.string.pref_password)) {
                    false -> it.summary = it.text
                    true -> it.summary = String(CharArray(it.text.length) { '*' })
                }
            }

    private fun checkValidity() {
        presenter.checkValidity(
            brokerHostPreference?.text,
            brokerPortPreference?.text?.toIntOrNull(),
            usernamePreference?.text,
            passwordPreference?.text,
            clientIdPreference?.text,
            reconnectPreference?.isChecked,
            cleanSessionPreference?.isChecked
        )
    }
}
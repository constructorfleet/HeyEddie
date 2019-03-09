package rocks.teagantotally.heartofgoldnotifications.presentation.config

import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import rocks.teagantotally.heartofgoldnotifications.R
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ConfigFragment : PreferenceFragmentCompat(), ConfigContract.View, CoroutineScope {
    override lateinit var presenter: ConfigContract.Presenter
    override lateinit var coroutineContext: CoroutineContext


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.connection_configuration, rootKey)
    }

    override fun setHost(host: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPort(port: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setUsername(usernmae: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPassword(password: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setClientId(clientId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setReconnect(reconnect: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCleanSession(cleanSession: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun showLoading(loading: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
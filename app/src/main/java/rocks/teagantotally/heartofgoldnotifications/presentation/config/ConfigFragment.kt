package rocks.teagantotally.heartofgoldnotifications.presentation.config

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ConfigFragment : Fragment(), ConfigContract.View, CoroutineScope {
    override lateinit var presenter: ConfigContract.Presenter
    override lateinit var coroutineContext: CoroutineContext

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
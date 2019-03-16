package rocks.teagantotally.heartofgoldnotifications.presentation.main

import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_COMMAND_FAILED
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_CONNECTED
import rocks.teagantotally.heartofgoldnotifications.data.services.MqttService.Companion.EVENT_DISCONNECTED
import rocks.teagantotally.heartofgoldnotifications.data.services.receivers.MqttEventBroadcastReceiver
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ConnectionViewState
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Navigable
import rocks.teagantotally.heartofgoldnotifications.presentation.config.ConfigFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.history.HistoryFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.main.injection.MainActivityComponent
import rocks.teagantotally.heartofgoldnotifications.presentation.main.injection.MainActivityModule
import javax.inject.Inject


class MainActivity : BaseActivity(),
    MainActivityContract.View,
    FragmentManager.OnBackStackChangedListener {

    companion object {
        lateinit var mainActivityComponent: MainActivityComponent
    }

    @Inject
    override lateinit var presenter: MainActivityContract.Presenter

    private val eventBroadcastReceiver: MqttEventBroadcastReceiver<MainActivityContract.Presenter> by lazy {
        MqttEventBroadcastReceiver(presenter)
    }

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.main_container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        supportFragmentManager.addOnBackStackChangedListener(this)

        HeyEddieApplication.applicationComponent
            .mainActivityComponentBuilder()
            .module(MainActivityModule(this))
            .build()
            .also {
                mainActivityComponent = it
            }
            .inject(this)

        navigation_drawer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_connection ->
                    presenter.onHandleConnectionNavigation()
                R.id.menu_item_settings ->
                    presenter.onNavigateToConfigSettings()
                else -> null
            }
                .also { drawer_container.closeDrawers() }
                ?.let { true }
                ?: false
        }

        presenter.onViewCreated()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(
            eventBroadcastReceiver,
            IntentFilter()
                .apply {
                    addAction(EVENT_CONNECTED)
                    addAction(EVENT_DISCONNECTED)
                    addAction(EVENT_COMMAND_FAILED)
                }
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home ->
                drawer_container.openDrawer(GravityCompat.START)
            else -> null
        }
            ?.let { true }
            ?: false

    override fun onBackStackChanged() {
        currentFragment
            ?.let { it as? Navigable }
            ?.let { navigation_drawer.menu.findItem(it.navigationMenuId) }
            ?.run { isChecked = true }
            ?: uncheckNavigationItems()
    }

    override fun showConfigSettings() {
        setFragment(ConfigFragment(), true)
    }

    override fun showStatus() {
        setFragment(HistoryFragment(), true)
    }

    override fun showLoading(loading: Boolean) {
        loading_indicator.visibility =
            when (loading) {
                true -> View.VISIBLE
                false -> View.GONE
            }
    }

    override fun showError(message: String?) {
        message?.let {
            Snackbar.make(
                coordinator,
                it,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun setConnectionState(state: ConnectionViewState) {
        navigation_drawer.menu
            .let { menu ->
                when (state) {
                    ConnectionViewState.Connected ->
                        menu.findItem(R.id.menu_item_connection)
                            ?.let {
                                it.setIcon(R.drawable.ic_unsync)
                                it.setTitle(R.string.connection_disconnect)
                            }
                            ?.let {
                                menu.findItem(R.id.menu_item_publish)
                                    ?.let {
                                        it.isEnabled = true
                                        it.icon.alpha = 255
                                    }
                            }
                    ConnectionViewState.Disconnected ->
                        menu.findItem(R.id.menu_item_connection)
                            ?.let {
                                it.setIcon(R.drawable.ic_sync)
                                it.setTitle(R.string.connection_establish)
                            }
                            ?.let {
                                menu.findItem(R.id.menu_item_publish)
                                    ?.let {
                                        it.isEnabled = false
                                        it.icon.alpha = 127
                                    }
                            }
                    ConnectionViewState.Unconfigured ->
                        menu.findItem(R.id.menu_item_connection)
                            ?.let {
                                it.setIcon(R.drawable.ic_sync_problem)
                                it.setTitle(R.string.connection_not_possible)
                            }
                            ?.run {
                                menu.findItem(R.id.menu_item_publish)
                                    ?.let {
                                        it.isEnabled = false
                                        it.icon.alpha = 127
                                    }
                            }
                }
            }
            ?: Toast.makeText(
                this,
                "Unable to set connection state $state",
                Toast.LENGTH_SHORT
            ).show()
    }

    private fun uncheckNavigationItems() {
        navigation_drawer
            .menu
            .let {
                for (i in 0..(it.size() - 1)) {
                    it.getItem(i).isChecked = false
                }
            }
    }
}
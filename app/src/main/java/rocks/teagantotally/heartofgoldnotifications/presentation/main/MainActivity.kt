package rocks.teagantotally.heartofgoldnotifications.presentation.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.base.ConnectionViewState
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Navigable
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.config.ConfigFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.history.HistoryFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.main.injection.MainActivityComponent
import rocks.teagantotally.heartofgoldnotifications.presentation.main.injection.MainActivityModule
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.pubish.PublishFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.main.fragments.subscriptions.SubscriptionsFragment
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MainActivity : BaseActivity(),
    MainActivityContract.View,
    FragmentManager.OnBackStackChangedListener {

    companion object {
        lateinit var mainActivityComponent: MainActivityComponent
    }

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Main
    @Inject
    override lateinit var presenter: MainActivityContract.Presenter

    private var currentFragment: Fragment? = null
    private val setCurrentFragment: (Fragment) -> Unit = {
        currentFragment = it
        updateNavigationMenu()
    }

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

        navigation_drawer.setNavigationItemSelectedListener { menuItem ->
            drawer_container.closeDrawers()
                .let {
                    when (menuItem.itemId) {
                        R.id.menu_item_connection ->
                            presenter.onHandleConnectionNavigation()
                        R.id.menu_item_settings ->
                            presenter.onNavigateToConfigSettings()
                        R.id.menu_item_subscriptions ->
                            presenter.onNavigateToSubscriptions()
                        R.id.menu_item_history ->
                            presenter.onNavigateToHistory()
                        R.id.menu_item_publish ->
                            presenter.onNavigateToPublish()
                        else -> null
                    }
                        ?.let { true }
                        ?: false
                }
        }

        presenter.onViewCreated()
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
        updateNavigationMenu()
    }

    override fun showConfigSettings() {
        setFragment(
            ConfigFragment(),
            true,
            setCurrentFragment
        )
    }

    override fun showHistory() {
        setFragment(
            HistoryFragment(),
            true,
            setCurrentFragment
        )
    }

    override fun showSubscriptions() {
        setFragment(
            SubscriptionsFragment(),
            true,
            setCurrentFragment
        )
    }

    override fun showPublish() {
        setFragment(
            PublishFragment(),
            true,
            setCurrentFragment
        )
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

    private fun updateNavigationMenu() {
        navigation_drawer.menu
            .let {
                for (i in 0..(it.size() - 1)) {
                    it.getItem(i)
                        ?.let { menuItem ->
                            menuItem.isChecked =
                                (menuItem.itemId == (currentFragment as? Navigable)?.navigationMenuId)
                        }
                }
            }
    }
}
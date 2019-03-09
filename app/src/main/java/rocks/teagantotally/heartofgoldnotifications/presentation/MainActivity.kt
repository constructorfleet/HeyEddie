package rocks.teagantotally.heartofgoldnotifications.presentation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.app.HeartOfGoldNotificationsApplication
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Navigable
import rocks.teagantotally.heartofgoldnotifications.presentation.config.ConfigFragment
import rocks.teagantotally.heartofgoldnotifications.presentation.injection.MainActivityComponent
import rocks.teagantotally.heartofgoldnotifications.presentation.injection.MainActivityModule
import rocks.teagantotally.heartofgoldnotifications.presentation.status.StatusFragment
import javax.inject.Inject


class MainActivity : BaseActivity(),
    MainActivityContract.View,
    FragmentManager.OnBackStackChangedListener {

    companion object {
        lateinit var mainActivityComponent: MainActivityComponent
    }
//    @Inject
//    lateinit var client: Client
//
//    @Inject
//    lateinit var configProvider: ConnectionConfigProvider
//
//    val eventChannel = Channel<ClientEvent>()
//
//    var messageChannel = Channel<MessageEvent>()

    @Inject
    override lateinit var presenter: MainActivityContract.Presenter

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

        HeartOfGoldNotificationsApplication.applicationComponent
            .mainActivityComponentBuilder()
            .module(MainActivityModule(this))
            .build()
            .also {
                mainActivityComponent = it
            }
            .inject(this)

        navigation_drawer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_settings ->
                    presenter.onNavigateToConfigSettings()
                        .run { true }

                else -> false
            }.also { drawer_container.closeDrawers() }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, StatusFragment())
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .commit()

//        client.connect(configProvider.getConnectionConfiguration())
    }

//    override fun onResume() {
//        super.onResume()
//        launch {
//            while (!eventChannel.isClosedForReceive) {
//                eventChannel.receiveOrNull()
//                    ?.let {
//                        when (it.type) {
//                            is ClientEventType.Connection ->
//                                client.subscribe("/test", 0)
//                        }
//                    }
//            }
//        }
//
//        launch {
//            while (!messageChannel.isClosedForReceive) {
//                messageChannel.receiveOrNull()
//                    ?.let {
//                        when (it) {
//                            is MessageEvent.Received.Success -> Toast.makeText(
//                                this@MainActivity,
//                                String(it.message.payload),
//                                Toast.LENGTH_LONG
//                            ).show()
//                            is MessageEvent.Received.Failure -> Timber.d("{${it.throwable}")
//                            else -> return@let
//                        }
//                    }
//            }
//        }
//    }

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
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, ConfigFragment(), ConfigFragment.TAG)
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .addToBackStack(ConfigFragment.TAG)
            .commit()
    }

    override fun showLoading(loading: Boolean) {
        loading_indicator.visibility =
            when (loading) {
                true -> View.VISIBLE
                false -> View.GONE
            }
    }

    override fun showError(message: String?) {
        // TODO
    }

    private fun uncheckNavigationItems() {
        navigation_drawer
            .menu
            .let {
                for (i in 0..it.size()) {
                    it.getItem(i).isChecked = false
                }
            }
    }
}
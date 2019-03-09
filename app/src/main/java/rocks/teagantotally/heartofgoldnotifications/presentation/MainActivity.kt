package rocks.teagantotally.heartofgoldnotifications.presentation

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.app.HeyEddieApplication
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
                R.id.menu_item_settings ->
                    presenter.onNavigateToConfigSettings()
                        .run { true }

                else -> false
            }.also { drawer_container.closeDrawers() }
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
        setFragment(StatusFragment(), true)
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

    private fun uncheckNavigationItems() {
        navigation_drawer
            .menu
            .let {
                for (i in 0..(it.size()-1)) {
                    it.getItem(i).isChecked = false
                }
            }
    }
}
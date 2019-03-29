package rocks.teagantotally.heartofgoldnotifications.presentation.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.app.managers.FragmentJobManager
import rocks.teagantotally.heartofgoldnotifications.presentation.common.annotations.ActionBarTitle
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.registerFragmentLifecycleCallbacks(FragmentJobManager, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(FragmentJobManager)
    }

    protected fun <FragmentType : Fragment> setFragment(
        fragment: FragmentType,
        addToBackStack: Boolean,
        onCommit: ((Fragment) -> Unit)? = null
    ) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment, fragment.tag)
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .also {
                if (addToBackStack) {
                    it.addToBackStack(fragment.tag)
                }
            }
            .commit()
            .run {
                onCommit?.let { it(fragment) }
            }
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        fragment
            ?.javaClass
            ?.getDeclaredAnnotation(ActionBarTitle::class.java)
            ?.let { supportActionBar?.title = getString(it.value) }
    }
}
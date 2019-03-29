package rocks.teagantotally.heartofgoldnotifications.app.managers

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.*
import rocks.teagantotally.heartofgoldnotifications.common.extensions.childScope
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseFragment

object FragmentJobManager : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentAttached(fragmentManager: FragmentManager, fragment: Fragment, context: Context) {
        (fragment as? BaseFragment)
            ?.let { scopedFragment ->
                (fragment.activity as? CoroutineScope)
                    ?.let { it.childScope(Dispatchers.Main) }
                    ?.let {
                        scopedFragment.coroutineScope = it
                    }
            }
    }

    override fun onFragmentDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
        (fragment as? CoroutineScope)
            ?.coroutineContext
            ?.get(Job)
            ?.also {
                Timber.d { "Cancelling ${it.children.count()} jobs" }
            }
            ?.run {
                try {
                    cancelChildren()
                } catch (e: CancellationException) {
                    Timber.e(e)
                }
            }
    }
}
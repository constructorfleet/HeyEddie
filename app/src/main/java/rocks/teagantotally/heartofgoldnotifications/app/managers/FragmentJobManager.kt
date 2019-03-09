package rocks.teagantotally.heartofgoldnotifications.app.managers

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped

object FragmentJobManager : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentAttached(fragmentManager: FragmentManager, fragment: Fragment, context: Context) {
        (fragment as? Scoped)
            ?.let {scopedFragment ->
                (fragment.activity as? Scoped)
                    ?.let { it.job }
                    .let {
                        with(scopedFragment) {
                            job = Job(it)
                        }
                    }
            }
    }

    override fun onFragmentDetached(fragmentManager: FragmentManager, fragment: Fragment) {
        (fragment as? Scoped)
            ?.job
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
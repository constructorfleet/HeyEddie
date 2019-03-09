package rocks.teagantotally.heartofgoldnotifications.app.managers

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancelChildren
import rocks.teagantotally.heartofgoldnotifications.presentation.base.BaseActivity
import rocks.teagantotally.heartofgoldnotifications.presentation.base.Scoped

object ActivityJobManager : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity?) {
        // no-op
    }

    override fun onActivityResumed(activity: Activity?) {
        // no-op
    }

    override fun onActivityStarted(activity: Activity?) {
        // no-op
    }

    override fun onActivityDestroyed(activity: Activity?) {
        (activity as? Scoped)
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

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        // no-op
    }

    override fun onActivityStopped(activity: Activity?) {
        // no-op
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        // no-op
    }
}
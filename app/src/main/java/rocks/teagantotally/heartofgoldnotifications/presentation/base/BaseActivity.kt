package rocks.teagantotally.heartofgoldnotifications.presentation.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import rocks.teagantotally.heartofgoldnotifications.R
import rocks.teagantotally.heartofgoldnotifications.app.managers.FragmentJobManager
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity : AppCompatActivity(), Scoped {
    override var job: Job = Job()
    override val coroutineContext: CoroutineContext =
        job.plus(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.registerFragmentLifecycleCallbacks(FragmentJobManager, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(FragmentJobManager)
    }

    protected fun <FragmentType> setFragment(
        fragment: FragmentType,
        addToBackStack: Boolean
    ) where FragmentType : Fragment, FragmentType : Scoped {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment, fragment.tag)
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .also {
                if (addToBackStack) {
                    it.addToBackStack(fragment.tag)
                }
            }
            .commit()
    }
}
package rocks.teagantotally.heartofgoldnotifications.presentation.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
}
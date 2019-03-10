package rocks.teagantotally.heartofgoldnotifications.presentation.base

import android.support.v4.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment : Fragment(), Scoped {
    override lateinit var job: Job
    override val coroutineContext: CoroutineContext by lazy { job.plus(Dispatchers.Main) }
}